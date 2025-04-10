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


def generate(sine_freq_low, sample_freq, ampl, offset, bit_depth):
    num_samples = int(sample_freq / sine_freq_low)  # number of samples per period
    t = np.arange(num_samples) / sample_freq
    print(t.size)
    scale_factor = (1 << (bit_depth - 1))  # scale factor for signed fixed-point range [-2^23, 2^23 - 1]
    sine_wave = ampl * np.sin(2 * np.pi * sine_freq_low * t) + offset
    np.random.seed(42)
    noise = 0.5 * ampl * np.random.normal(size=len(t))
    noisy_sine_wave = sine_wave + noise
    noisy_sine_wave_int = np.round(noisy_sine_wave * scale_factor).astype(int)
    plot(noisy_sine_wave, t, "sine")
    plot(noisy_sine_wave_int, t, "sine_int")
    print("Sine signal has been plotted in sine.png")
    return noisy_sine_wave_int, t

def write_verilog(filename, sine_signal, data_width):
    with open(filename, "w") as f:
        f.write("// Sine wave samples generated by Python\n")
        f.write(f"// wire signed [{data_width-1}:0] sine_signal [{len(sine_signal)-1}:0];\n\n")
        for i, sample in enumerate(sine_signal):
            hex_value = f"{(sample & ((1 << data_width) - 1)):0{data_width // 4}X}"
            f.write(f"assign sine_signal[{i}] = {data_width}'h{hex_value};\n")
            # print(f"Sample {i}: {sample} -> Hex: {hex_value}")
    print("Verilog header generated as sine_signal.vh.")

# Function to parse the config file into a dictionary
def parse_config(file_path):
    parameters = {}
    try:
        with open(file_path, "r") as f:
            for line in f:
                # Skip empty lines and comments
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                
                # Split key and value
                key, value = line.split("=", 1)
                parameters[key.strip()] = int(value.strip())  # Convert to integer
    except FileNotFoundError:
        raise FileNotFoundError(f"Config file not found: {file_path}")
    except ValueError as e:
        raise ValueError(f"Error parsing line in config file: {line}. Details: {e}")
    return parameters

def main():
    # Parameters
    sine_freq = 200 # (Hz)
    sample_freq = 44000  # (Hz)
    amplitude = 0.25  # (range between -1 and 1)
    offset = 0  # y-axis offset of the sine wave

    DESIGN_DIR = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/"
    PARAMS_FILE = DESIGN_DIR + "parameters_top_level.txt"
    parameters = parse_config(PARAMS_FILE)

    bit_depth = parameters.get("DATA_WIDTH", None)
    MAX_VALUE = (1 << (bit_depth - 1)) - 1  # 8388607 for 24-bit signed
    MIN_VALUE = -(1 << (bit_depth - 1))     # -8388608 for 24-bit signed

    if amplitude > 1 or amplitude < -1:
        print(f"Amplitude {amplitude} must be between -1 and 1")
        exit()
    
    sine_wave_int, t = generate(sine_freq, sample_freq, amplitude, offset, bit_depth)
    sine_wave_int = np.clip(sine_wave_int, MIN_VALUE, MAX_VALUE)  # clipping to range

    filename = DESIGN_DIR + "verif/sine_signal.vh"
    
    write_verilog(filename, sine_wave_int, bit_depth)


if __name__ == '__main__':
    main()
