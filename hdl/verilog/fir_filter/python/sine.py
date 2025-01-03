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
    print(t.size)
    scale_factor = (1 << (bit_depth - 1))  # scale factor for signed fixed-point range [-2^23, 2^23 - 1]
    sine_wave = ampl * np.sin(2 * np.pi * sine_freq_low * t) + 0.5 * ampl * np.sin(2 * np.pi * sine_freq_high * t) + offset
    sine_wave_int = np.round(sine_wave * scale_factor).astype(int)

    plot(sine_wave, t, "sine")
    plot(sine_wave_int, t, "sine_int")
    print("Sine signal has been plotted in sine.png")

    # for s in sine_wave:
    #     print(s)
    # for s in sine_wave_int:
    #     print(s)
    return sine_wave_int, t

def write_verilog(filename, sine_signal, data_width):
    with open(filename, "w") as f:
        f.write("// Sine wave samples generated by Python\n")
        f.write(f"// wire signed [{data_width-1}:0] sine_signal [{len(sine_signal)-1}:0];\n\n")

        for i, sample in enumerate(sine_signal):
            hex_value = f"{(sample & ((1 << data_width) - 1)):0{data_width // 4}X}"
            f.write(f"assign sine_signal[{i}] = {data_width}'h{hex_value};\n")
            # print(f"Sample {i}: {sample} -> Hex: {hex_value}")

    print("Verilog header generated as sine_signal.vh.")



def main():
    # Parameters
    sine_freq_high = 5000  # (Hz)
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

    design_dir = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/"
    filename = design_dir + "verif/sine_signal.vh"
    
    write_verilog(filename, sine_wave_int, bit_depth)


if __name__ == '__main__':
    main()
