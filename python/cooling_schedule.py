"""
plot_numbers.py
Read a file of newline-delimited numbers and save a line-chart PNG.

Usage:
    python plot_numbers.py <input.txt> [output.png]
"""
import sys
from pathlib import Path
import matplotlib.pyplot as plt

def main() -> None:
    # ---- parse arguments ---------------------------------------------------
    if len(sys.argv) not in (2, 3):
        print("Usage: python plot_numbers.py <input.txt> [output.png]", file=sys.stderr)
        sys.exit(1)

    in_path  = Path(sys.argv[1])
    out_path = Path(sys.argv[2]) if len(sys.argv) == 3 else in_path.with_suffix(".png")

    # ---- read data ---------------------------------------------------------
    try:
        with in_path.open() as f:
            values = [float(line.strip()) for line in f if line.strip()]
    except FileNotFoundError:
        sys.exit(f"Error: {in_path} not found.")
    except ValueError as e:
        sys.exit(f"Error parsing numbers: {e}")

    if not values:
        sys.exit("Error: no numeric data found.")

    # ---- plot --------------------------------------------------------------
    plt.figure(figsize=(4, 2.5))
    plt.plot(range(len(values)), values, linewidth=1.5)
    plt.title(in_path.name)
    plt.xlabel("Passes")
    plt.ylabel("Temperature")
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(out_path, dpi=150)
    plt.close()
    print(f"Saved plot to {out_path}")

if __name__ == "__main__":
    main()
