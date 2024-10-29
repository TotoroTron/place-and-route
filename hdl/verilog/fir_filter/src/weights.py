
import numpy as np
import matplotlib.pyplot as plt
from scipy.signal import firwin

def generate(filter_depth, data_width, cutoff_freq, sample_rate):
    # Design the FIR filter with the given parameters
    fir_coefficients = firwin(filter_depth, cutoff_freq, fs=sample_rate, pass_zero=True)
    # Scale and quantize the coefficients based on the data width
    max_val = 2**(data_width - 1) - 1
    quantized_coefficients = np.round(fir_coefficients * max_val).astype(int)
    # Ensure coefficients fit within the data width by clipping
    quantized_coefficients = np.clip(quantized_coefficients, -max_val, max_val)

    return quantized_coefficients


def plot(weights):
    plt.figure(figsize=(10, 4))
    plt.stem(range(len(weights)), weights, basefmt=" ")
    plt.title("FIR Filter Weights")
    plt.xlabel("Weight Index")
    plt.ylabel("Amplitude")
    plt.grid(True)
    plt.savefig("weights.png")
    print("Fir filter weights plotted in weights.png")


def write_mem(weights, DATA_WIDTH):
    # Open the file to write weights in hexadecimal format
    with open("weights.mem", "w") as f:
        for i, weight in enumerate(weights):
            # Convert each weight to hexadecimal, keeping only the lower DATA_WIDTH bits
            hex_value = f"{(weight & ((1 << DATA_WIDTH) - 1)):0{DATA_WIDTH // 4}X}"
            # f.write(f"@{i:02X} {hex_value}\n")
            f.write(f"{hex_value}\n")
    print("FIR filter weights written to weights.mem in hexadecimal format.")


def write_verilog(weights, data_width):
    # Open a Verilog package file for writing
    with open("weights_pkg.v", "w") as f:
        f.write("`ifndef WEIGHTS_PKG_V\n")
        f.write("`define WEIGHTS_PKG_V\n\n")
        f.write("package weights_pkg;\n\n")
        # Define a 2D array for weights in hexadecimal format
        f.write(f"    localparam logic signed [{data_width-1}:0] WEIGHTS [0:{len(weights)-1}] = '{{\n")
        # Write each weight as a hexadecimal value in the array
        for i, weight in enumerate(weights):
            hex_value = f"{(weight & ((1 << data_width) - 1)):0{data_width // 4}X}"
            f.write(f"        {data_width}'h{hex_value}")
            if i < len(weights) - 1:
                f.write(",\n")  # Add a comma after each entry except the last one
            else:
                f.write("\n")  # Last entry without a comma
        f.write("    };\n\n")
        f.write("endpackage : weights_pkg\n")
        f.write("`endif // WEIGHTS_PKG_V\n")
    print("Verilog package generated in weights_pkg.v")


def main():
    # Parameters
    FILTER_DEPTH = 128       # Number of filter taps
    DATA_WIDTH = 24          # Number of bits for each coefficient
    CUTOFF_FREQ = 0.2        # Normalized cutoff frequency (0.0 to 0.5)
    SAMPLE_RATE = 44000      # Sampling frequency

    # Generate FIR filter weights
    weights = generate(FILTER_DEPTH, DATA_WIDTH, CUTOFF_FREQ, SAMPLE_RATE)
    plot(weights)
    write_mem(weights, DATA_WIDTH)
    write_verilog(weights, DATA_WIDTH)

if __name__ == '__main__':
    main()
