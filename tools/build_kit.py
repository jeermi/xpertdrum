import subprocess, numpy as np, os, sys, wave, json
# Usage : python3 tools/build_kit.py <dossier_sortie> <dossier Samples de virtuosity_drums>
SRC = sys.argv[2] if len(sys.argv) > 2 else "virtuosity_drums/Samples"
OUT = sys.argv[1]
SR = 48000
MICS = {"kickmic": 1.0, "snaremic": 1.0, "oh": 1.0, "room": 0.5}

def load(path):
    raw = subprocess.run(["ffmpeg","-v","error","-i",path,"-f","f32le","-ac","2","-ar",str(SR),"-"],capture_output=True,check=True).stdout
    a = np.frombuffer(raw, dtype=np.float32).reshape(-1,2)
    return a.mean(axis=1)

def mix(folder, stem):
    parts = []
    for mic, g in MICS.items():
        p = f"{SRC}/{mic}/{folder}/{mic}_{stem}.flac"
        parts.append(load(p) * g)
    n = max(len(p) for p in parts)
    out = np.zeros(n, dtype=np.float64)
    for p in parts: out[:len(p)] += p
    return out

# lane: (folder, duration_s, {level: [stems]})
KIT = {
 "kick":  ("kick", 1.2, {"soft":["kick_snon_vl2_rr1","kick_snon_vl2_rr2"],"mid":["kick_snon_vl3_rr1","kick_snon_vl3_rr2"],"loud":["kick_snon_vl4_rr1","kick_snon_vl4_rr2"]}),
 "snare": ("snare", 1.0, {"soft":["snare_center_vl9","snare_center_vl11"],"mid":["snare_center_vl21","snare_center_vl23"],"loud":["snare_center_vl31","snare_center_vl33"]}),
 "hihat": ("hh", 0.6, {"soft":["hh_closed_vl2_rr1","hh_closed_vl2_rr2"],"mid":["hh_closed_vl3_rr1","hh_closed_vl3_rr2"],"loud":["hh_closed_vl4_rr1","hh_closed_vl4_rr2"]}),
 "hhfoot":("hh", 0.5, {"soft":["hh_pedal_vl2_rr1","hh_pedal_vl2_rr2"],"mid":["hh_pedal_vl3_rr1","hh_pedal_vl3_rr2"],"loud":["hh_pedal_vl3_rr3","hh_pedal_vl3_rr4"]}),
 "tom":   ("htom", 1.6, {"soft":["htom_center_vl5","htom_center_vl6"],"mid":["htom_center_vl11","htom_center_vl12"],"loud":["htom_center_vl15","htom_center_vl16"]}),
 "crash": ("crash", 3.5, {"soft":["crash_crash_vl1_rr1","crash_crash_vl1_rr2"],"mid":["crash_crash_vl2_rr1","crash_crash_vl2_rr2"],"loud":["crash_crash_vl3_rr1","crash_crash_vl3_rr2"]}),
 "ride":  ("ride", 2.5, {"soft":["ride_ride_vl1_rr1","ride_ride_vl1_rr2"],"mid":["ride_ride_vl2_rr1","ride_ride_vl2_rr2"],"loud":["ride_ride_vl3_rr1","ride_ride_vl3_rr2"]}),
}
rendered = {}
for lane,(folder,dur,levels) in KIT.items():
    for lvl, stems in levels.items():
        for i, stem in enumerate(stems):
            x = mix(folder, stem)
            # skip leading silence (keep 1 ms before onset)
            thr = np.abs(x).max()*0.02
            on = max(0, int(np.argmax(np.abs(x) > thr)) - SR//1000)
            x = x[on:on+int(dur*SR)]
            fade = int(min(len(x)*0.3, 0.25*SR))
            x[-fade:] *= np.linspace(1,0,fade)**2
            rendered[f"{lane}_{lvl}_{i+1}"] = (lane, x)
LEVEL = {"soft": 0.4, "mid": 0.75, "loud": 1.0}
TRIM = {"kick": 0.95, "snare": 0.9, "hihat": 1.0, "hhfoot": 0.45, "tom": 0.85, "crash": 0.6, "ride": 1.0}
os.makedirs(OUT, exist_ok=True)
info = {}
for k,(lane,x) in rendered.items():
    lvl = k.split("_")[1]
    y = x / np.abs(x).max() * LEVEL[lvl] * TRIM[lane]
    pcm = (np.clip(y,-1,1)*32767).astype("<i2")
    with wave.open(f"{OUT}/{k}.wav","wb") as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(pcm.tobytes())
    info[k] = round(float(np.sqrt((y[:SR//10]**2).mean())),3)
print(json.dumps(info))
