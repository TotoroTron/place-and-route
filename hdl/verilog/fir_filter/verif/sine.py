import numpy as np
import matplotlib.pyplot as plt

def generate_sine_table(sine_freq, sample_freq, ampl, offset, num_samples, bit_depth):
    """
    Generates a sine wave table in hexadecimal format.

    Parameters:
    sine_freq (float): Frequency of the sine wave in Hz.
    sample_freq (float): Sampling frequency in Hz.
    amplitude (int): Amplitude of the sine wave.
    offset (int): Offset to be added to the wave.
    num_samples (int): Number of samples to generate.

    Returns:
    list: A list of hexadecimal values representing the sine wave.
    """
    t = np.arange(num_samples) / sample_freq
    sine_wave = ampl * np.sin(2 * np.pi * sine_freq * t) + offset
    sine_wave_int = np.round(sine_wave).astype(int)
    return sine_wave_int, t

def plot_sine_table(sine_wave, t):
    plt.figure(figsize=(10, 4))
    plt.step(t, sine_wave, where="post", label="Step-wise Sine Wave", color="blue")
    plt.title("Step-wise Interpolation of Sine Wave")
    plt.xlabel("Time (s)")
    plt.ylabel("Amplitude")
    plt.grid(True)
    plt.legend()
    plt.savefig("sine.png")
    

def main():

    # Parameters
    sine_freq = 1000 # frequency of the sine wave (Hz)
    sample_freq = 44000 # sampling frequency (Hz)
    amplitude = 1000 # amplitude of the sine wave
    offset = 0 # offset of the sine wave
    num_samples = sample_freq / sine_freq # number of samples per period 
    bit_depth = 24 # +8388607 downto -8388608
    MAX_VALUE = np.power(2, bit_depth-1)-1
    MIN_VALUE = -np.power(2, bit_depth-1)

    if (amplitude > MAX_VALUE or amplitude < MIN_VALUE):
        print(f"Amplitude {amplitude} must be between {MIN_VALUE} and {MAX_VALUE}");
        exit()

    sine_wave_int, t = generate_sine_table(sine_freq, sample_freq, amplitude, offset, num_samples, bit_depth);
    sine_wave_hex = [f"{(val & ((1 << bit_depth) - 1)):0{bit_depth // 4}X}" for val in sine_wave_int]
    plot_sine_table(sine_wave_int, t)

    f = open("sine.mem", "w")
    for i, hex_val in enumerate(sine_wave_hex):
        f.write(f"@{i:02X} {hex_val}\n")
    f.close()



if __name__ == '__main__':
    main()
