
# import rapidwright
import jpype
import jpype.imports
from jpype.types import *


jpype.startJVM(classpath=[
    "/home/bcheng/workspace/tools/RapidWright/bin",
    "/home/bcheng/workspace/tools/RapidWright/jars/*"
])

from com.xilinx.rapidwright.device import *
from com.xilinx.rapidwright.design import *
from com.xilinx.rapidwright.rwroute import *
from com.xilinx.rapidwright.router import Router

# https://jpype.readthedocs.io/en/latest/quickguide.html
# https://docs.amd.com/r/en-US/ug912-vivado-properties/CELL

# https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/device/Device.html
# getDevice(Part part) OR getDevice(String partName)
device = Device.getDevice("xc7z020clg400-1")
print("Device Name: ", device.getName())

# https://www.rapidwright.io/javadoc/com/xilinx/rapidwright/design/Design.html
# Design(String designName, String partName)
design = Design("orGate", device.getName())
print("Design Name: ", design.getName())


"""
part: xc7z020
Buttons:
    set_property -dict { PACKAGE_PIN D19    IOSTANDARD LVCMOS33 } [get_ports { btn[0] }]; #IO_L4P_T0_35 Sch=BTN0
    set_property -dict { PACKAGE_PIN D20    IOSTANDARD LVCMOS33 } [get_ports { btn[1] }]; #IO_L4N_T0_35 Sch=BTN1

LEDs:
    set_property -dict { PACKAGE_PIN R14    IOSTANDARD LVCMOS33 } [get_ports { ov_led[0] }]; #IO_L6N_T0_VREF_34 Sch=LED0
    set_property -dict { PACKAGE_PIN P14    IOSTANDARD LVCMOS33 } [get_ports { ov_led[1] }]; #IO_L6P_T0_34 Sch=LED1
    set_property -dict { PACKAGE_PIN N16    IOSTANDARD LVCMOS33 } [get_ports { ov_led[2] }]; #IO_L21N_T3_DQS_AD14N_35 Sch=LED2
    set_property -dict { PACKAGE_PIN M14    IOSTANDARD LVCMOS33 } [get_ports { ov_led[3] }]; #IO_L23P_T3_35 Sch=LED3
"""

# Create and place a 2-input OR gate at the C6LUT in the slice at 50x50y.
or2 = design.createAndPlaceCell("or2", Unisim.OR2, "SLICE_X50Y50/C6LUT")

# Place the two button IO buffers
button0 = design.createAndPlaceIOB("button0", PinType.IN, "D19", "LVCMOS33")
button1 = design.createAndPlaceIOB("button1", PinType.IN, "D20", "LVCMOS33")


# Place the led IO buffer
led = design.createAndPlaceIOB("led", PinType.OUT, "R14", "LVCMOS33")


# Create a net to connect the button0 buffer to one of the OR gate inputs
button0Net = design.createNet("button0Net")
button0Net.connect(button0, "O")
button0Net.connect(or2, "I0")

# Create a net to connect the button1 buffer to the other OR gate input
button1Net = design.createNet("button1Net")
button1Net.connect(button1, "O")
button1Net.connect(or2, "I1")


# Create a net to connect the output of the OR gate to the led
ledNet = design.createNet("ledNet")
ledNet.connect(or2, "O")
ledNet.connect(led, "I")

# Route the connections internal to the sites
design.routeSites()

# Use the basic router to route the design
router = Router(design)
router.routeDesign()

# Write the design out to a checkpoint
design.writeCheckpoint("or_gate.dcp")

jpype.shutdownJVM()