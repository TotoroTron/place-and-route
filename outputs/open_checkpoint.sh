#!/bin/bash

echo "open_checkpoint $1; start_gui" | vivado -nolog -nojournal -mode tcl
