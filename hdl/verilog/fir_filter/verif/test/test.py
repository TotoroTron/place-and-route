import numpy as np
from numpy import arange, pi  # For generating signals
from numpy.random import rand  # For generating random noise
import matplotlib.pyplot as plt  # For plotting
from scipy.signal import firwin, lfilter  # For FIR filter design and filtering

# Sampling and filter parameters
FS = 44000  # Sampling rate in Hz
CF = 1000  # Cutoff frequency in Hz
FC = CF / (0.5 * FS)  # Normalized cutoff frequency (Nyquist frequency = 0.5 * FS)
N = 128  # Number of filter taps
a = 1  # Filter denominator (for FIR filters)

# FIR filter design
b = firwin(N, cutoff=FC, window='hamming')  # Use firwin directly from scipy.signal

# Signal generation parameters
M = int(CF)  # Generate 1 second of signal
n = arange(M)  # Time index (samples)
t = n / FS  # Time in seconds
ampl = 0.2  # Amplitude of the sine waves
sine_freq_low = 200  # Frequency of the low sine wave in Hz
sine_freq_high = 2000  # Frequency of the high sine wave in Hz

# Generate the signal: two sine waves + random noise
x1 = ampl * np.sin(2 * pi * sine_freq_low * t)  # Low-frequency sine wave
x2 = ampl * np.sin(2 * pi * sine_freq_high * t)  # High-frequency sine wave
x = x1 + x2 # + 0.5 * rand(M)  # Signal = low + high sine waves + noise

# Apply the FIR filter
y = lfilter(b, a, x)  # Filtered output

# Plot the original and filtered signals
plt.figure(figsize=(10, 6))
plt.plot(t, x, label="Original Signal", alpha=0.6)
plt.plot(t, y, 'r', label="Filtered Signal", linewidth=1.5)
plt.xlabel("Time (s)")
plt.ylabel("Amplitude")
plt.title("Signal and Filtered Output")
plt.legend()
plt.grid()
plt.tight_layout()
plt.show()
