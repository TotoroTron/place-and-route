#!/bin/bash

source /home/bcheng/workspace/tools/venvs/rapid-wright/bin/activate

python3 or_gate.py

vivado -mode batch -source or_gate.tcl -nolog -nojournal
