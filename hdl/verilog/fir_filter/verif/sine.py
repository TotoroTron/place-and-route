import numpy as np
import matplotlib.pyplot as plt

def plot(sine_wave, t, fname):
    plt.figure(figsize=(10, 4))
    plt.step(t, sine_wave, where="post", label="Step-wise Sine Wave", color="blue")
    plt.title("Step-wise Interpolation of Sine Wave")
    plt.xlabel("Time (s)")
    plt.ylabel("Amplitude")
    plt.grid(True)
    plt.legend()
    plt.savefig(f"{fname}.png")


def generate(sine_freq_high, sine_freq_low, sample_freq, ampl, offset, bit_depth):
    num_samples = int(sample_freq / sine_freq_low)  # number of samples per period
    t = np.arange(num_samples) / sample_freq
    scale_factor = (1 << (bit_depth - 1))  # scale factor for signed fixed-point range [-2^23, 2^23 - 1]
    sine_wave = ampl * np.sin(2 * np.pi * sine_freq_low * t) + ampl * np.sin(2 * np.pi * sine_freq_high * t) + offset
    sine_wave_int = np.round(sine_wave * scale_factor).astype(int)

    plot(sine_wave, t, "sine")
    plot(sine_wave_int, t, "sine_int")
    print("Sine signal has been plotted in sine.png")

    # for s in sine_wave:
    #     print(s)
    # for s in sine_wave_int:
    #     print(s)
    return sine_wave_int, t


def main():
    # Parameters
    sine_freq_high = 2000  # (Hz)
    sine_freq_low = 200 # (Hz)
    sample_freq = 44000  # (Hz)
    amplitude = 0.2  # (range between -1 and 1)
    offset = 0  # y-axis offset of the sine wave
    bit_depth = 24  # Using 24-bit signed fixed-point representation
    MAX_VALUE = (1 << (bit_depth - 1)) - 1  # 8388607 for 24-bit signed
    MIN_VALUE = -(1 << (bit_depth - 1))     # -8388608 for 24-bit signed

    if amplitude > 1 or amplitude < -1:
        print(f"Amplitude {amplitude} must be between -1 and 1")
        exit()
    
    sine_wave_int, t = generate(sine_freq_high, sine_freq_low, sample_freq, amplitude, offset, bit_depth)
    sine_wave_int = np.clip(sine_wave_int, MIN_VALUE, MAX_VALUE)  # clipping to range
    sine_wave_hex = [f"{(val & ((1 << bit_depth) - 1)):0{bit_depth // 4}X}" for val in sine_wave_int]
    

    # Write to file in hexadecimal format
    with open("sine.mem", "w") as f:
        for i, hex_val in enumerate(sine_wave_hex):
            f.write(f"@{i:02X} {hex_val}\n")


if __name__ == '__main__':
    main()
