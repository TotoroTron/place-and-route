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
from com.xilinx.rapidwright.site import *

device = Device.getDevice("xc7z020clg400-1")
print("Device Name: ", device.getName())

# get all sites SLICEM sites
slices = device.getAllSitesOfType(SiteTypeEnum.SLICEM)

# put all BELs in all slices that are LUTs and check the length
numOfLUTs = len([bel for site in slices for bel in site.getBELs() if bel.isLUT()])
# if bel.isLUT() then append(bel)

print("Number of LUTS:", numOfLUTs)


project_dir = "/home/bcheng/workspace/dev/place-and-route/"
synthesized_dcp = f"{project_dir}/tcl/synthesized.dcp"
placed_dcp = f"{project_dir}/tcl/placed.dcp"

def calculate_cost(design):
    # HPWL manhattan
    cost = 0
    for net in design.getNets():
        for pin in net.getPins():
            # rwroute/RouteNodeInfo.java
            # design/TileRectangle.java
            # loc = pin.getCell().getSite().getSiteXY()
            # cost += abs(loc.X) + abs(loc.Y)
    return cost





design = Design.readCheckpoint(synthesized_dcp)

initial_cost = calculate_cost(design)
current_cost = initial_cost
best_cost = initial_cost
# best_design = design.clone()





jpype.shutdownJVM()

