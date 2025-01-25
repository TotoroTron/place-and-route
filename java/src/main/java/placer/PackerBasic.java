package placer;

import java.io.FileWriter;
import java.io.IOException;

import java.util.stream.Collectors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.xilinx.rapidwright.edif.EDIFCellInst;
import com.xilinx.rapidwright.edif.EDIFPortInst;
import com.xilinx.rapidwright.edif.EDIFHierCellInst;
import com.xilinx.rapidwright.edif.EDIFHierPortInst;
import com.xilinx.rapidwright.edif.EDIFHierNet;
import com.xilinx.rapidwright.design.Design;
import com.xilinx.rapidwright.device.Device;

public class PackerBasic extends Packer {
    public PackerBasic(String rootDir, Design design, Device device) throws IOException {
        super(rootDir, design, device);
        packerName = "PackerBasic";
        writer = new FileWriter(rootDir + "outputs/printout/" + packerName + ".txt");
        writer.write(packerName + ".txt");
    }

    public PackedDesign packDesign() throws IOException {

        // Create a map to group cells by type
        Map<String, List<EDIFHierCellInst>> EDIFCellGroups = new HashMap<>();
        Set<String> uniqueEdifCellTypes = new HashSet<>();

        // Organize EDIFHierCellInsts into "groups", where group labels are:
        // RAMB18E1, DSP18E1, CARRY4, FDRE, FDSE, LUT (LUT2-6 all in one group), etc.
        for (EDIFHierCellInst ehci : design.getNetlist().getAllLeafHierCellInstances()) {
            String cellType = ehci.getInst().getCellType().getName();
            // group all luts together
            if (cellType.contains("LUT"))
                cellType = "LUT";
            // populate unique cell types
            if (uniqueEdifCellTypes.add(cellType)) // set returns bool
                EDIFCellGroups.put(cellType, new ArrayList<>()); // spawn unique group
            // add cell to corresponding group
            EDIFCellGroups.get(cellType).add(ehci); // add cell to corresponding group
        }

        writer.write("\n\nSet of all Unique EDIF Cell Types... (" + uniqueEdifCellTypes.size() + ")");
        for (String edifCellType : uniqueEdifCellTypes) {
            writer.write("\n\t" + edifCellType);
        }
        writer.write("\nPrinting EDIFCells By Type...");
        for (Map.Entry<String, List<EDIFHierCellInst>> entry : EDIFCellGroups.entrySet()) {
            writer.write("\n\n" + entry.getKey() + " Cells (" + entry.getValue().size() + "):");
            List<EDIFCellInst> cells = entry.getValue().stream()
                    .map(e -> e.getInst())
                    .collect(Collectors.toList());
            printEDIFCellInstList(cells);
        }
        // List<DSPPair> = findDSPPairs(EDIFCellGroups);
        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs = findDSPPairs(EDIFCellGroups);
        List<EDIFHierCellInst> RAMCells = EDIFCellGroups.get("RAMB18E1");
        List<List<CarryCellGroup>> CARRYChains = findCarryChains(EDIFCellGroups);
        Map<Pair<String, String>, LUTFFGroup> LUTFFGroups = findLUTFFGroups(EDIFCellGroups);
        List<List<EDIFHierCellInst>> LUTGroups = buildLUTGroups(EDIFCellGroups);

        PackedDesign packedDesign = new PackedDesign(DSPPairs, RAMCells, CARRYChains, LUTFFGroups, LUTGroups);

        printCARRYChains(CARRYChains);
        printDSPPairs(DSPPairs);
        printLUTFFGroups(LUTFFGroups);
        printLUTGroups(LUTGroups);

        writer.write("\n\nPrinting remaining cells in EDIFCellGroups...");
        for (Map.Entry<String, List<EDIFHierCellInst>> entry : EDIFCellGroups.entrySet()) {
            writer.write("\n\tGroup: " + entry.getKey() + "... (" + entry.getValue().size() + ")");
            if (entry.getValue().isEmpty())
                writer.write("\n\t\tEmpty!");
            for (EDIFHierCellInst ehci : entry.getValue()) {
                writer.write("\n\t\tUnplaced Cell: " + ehci.getCellType() + ": " + ehci.getFullHierarchicalInstName());
            }
        }

        return packedDesign;
    }

    private List<Pair<EDIFHierCellInst, EDIFHierCellInst>> findDSPPairs(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> visitedDSPs = new ArrayList<>();
        List<Pair<EDIFHierCellInst, EDIFHierCellInst>> pairs = new ArrayList<>();

        if (EDIFCellGroups.get("DSP48E1") == null) {
            writer.write("\nWARNING: This design has zero DSP48E1 cells!\n");
            System.out.println("WARNING: This design has zero DSP48E1 cells!");
            return pairs;
        }

        for (EDIFHierCellInst DSP_CIN : EDIFCellGroups.get("DSP48E1")) {
            List<EDIFHierPortInst> pcins = DSP_CIN.getHierPortInsts().stream()
                    .filter(ehpi -> ehpi.getPortInst().getName().contains("PCIN"))
                    .collect(Collectors.toList());
            if (pcins.isEmpty())
                continue;
            // there should only be one source
            List<EDIFHierPortInst> pcouts = pcins.stream()
                    .map(cin -> cin.getHierarchicalNet().getLeafHierPortInsts(true, false).get(0))
                    .collect(Collectors.toList());
            // find the set of DSP cells that connect to the pcout ports
            Set<EDIFHierCellInst> DSP_COUT_SET = pcouts.stream()
                    .map(cout -> cout.getHierarchicalInst().getChild(cout.getPortInst().getCellInst().getName()))
                    .filter(cell -> cell.getInst().getCellName().equals("DSP48E1"))
                    .collect(Collectors.toSet());
            if (DSP_COUT_SET.size() < 1)
                continue;
            if (DSP_COUT_SET.size() > 1) {
                System.out.println("WARNING: DSP Cell " + DSP_CIN.getFullHierarchicalInstName()
                        + " has multiple DSP cells on PCIN bus!");
                writer.write("\nWARNING: DSP Cell " + DSP_CIN.getFullHierarchicalInstName()
                        + " has multiple DSP cells on PCIN bus!");
            }
            EDIFHierCellInst DSP_COUT = DSP_COUT_SET.stream().collect(Collectors.toList()).get(0);
            visitedDSPs.add(DSP_CIN);
            visitedDSPs.add(DSP_COUT);
            var pair = new Pair<EDIFHierCellInst, EDIFHierCellInst>(DSP_COUT, DSP_CIN);
            pairs.add(pair);
        }
        EDIFCellGroups.get("DSP48E1").removeAll(visitedDSPs);
        return pairs;
    }

    private List<List<CarryCellGroup>> findCarryChains(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) {
        List<String> OPorts = new ArrayList<>(List.of("O[0]", "O[1]", "O[2]", "O[3]"));
        List<String> SPorts = new ArrayList<>(List.of("S[0]", "S[1]", "S[2]", "S[3]"));
        List<List<CarryCellGroup>> carryCellGroupChains = new ArrayList<>();
        // find the carry chain anchor
        while (!EDIFCellGroups.get("CARRY4").isEmpty()) { // iterating over chains
            List<EDIFHierCellInst> chain = new ArrayList<>();
            EDIFHierCellInst currCell = EDIFCellGroups.get("CARRY4").get(0);

            // every iteration, EDIFCarryCells gets updated so .get(0) is different.
            while (true) {
                EDIFHierPortInst currCellPort = currCell.getPortInst("CI");
                EDIFHierNet hnet = currCellPort.getHierarchicalNet();
                if (hnet.getNet().isGND())
                    break;
                Collection<EDIFHierPortInst> netPorts = hnet.getPortInsts();
                Map<String, EDIFHierPortInst> netPortsMap = netPorts.stream()
                        .collect(Collectors.toMap(
                                p -> p.getPortInst().getName(),
                                p -> p));
                EDIFHierPortInst sourceCellPort = netPortsMap.get("CO[3]");
                EDIFHierCellInst sourceCell = sourceCellPort.getHierarchicalInst()
                        .getChild(sourceCellPort.getPortInst().getCellInst().getName());
                currCell = sourceCell;
            }

            // we now have the starting carry cell as currCell
            // now traverse in the cout direction
            // the end of the chain occurs when portinst CO[3] is null
            List<CarryCellGroup> carryCellGroups = new ArrayList<>();
            while (true) { // iterating through the chain itself
                chain.add(currCell); // currCell = carry cell
                CarryCellGroup carryCellGroup = new CarryCellGroup(currCell, new ArrayList<>(), new ArrayList<>());

                // assemble the carry site cells
                for (int i = 0; i < 4; i++) {
                    EDIFHierNet ONet = currCell.getPortInst(OPorts.get(i)).getHierarchicalNet();
                    EDIFHierPortInst ONetSink = ONet.getLeafHierPortInsts(false, true).get(0);
                    EDIFHierCellInst ONetSinkCell = ONetSink.getHierarchicalInst()
                            .getChild(ONetSink.getPortInst().getCellInst().getName());
                    if (ONetSinkCell.getCellType().getName().contains("FDRE"))
                        carryCellGroup.ffs().add(ONetSinkCell);
                    else
                        carryCellGroup.ffs().add(null);

                    EDIFHierNet SNet = currCell.getPortInst(SPorts.get(i)).getHierarchicalNet();
                    EDIFHierPortInst SNetSource = SNet.getLeafHierPortInsts(true, false).get(0);
                    EDIFHierCellInst SNetSourceCell = SNetSource.getHierarchicalInst()
                            .getChild(SNetSource.getPortInst().getCellInst().getName());
                    if (SNetSourceCell.getCellType().getName().contains("LUT"))
                        carryCellGroup.luts().add(SNetSourceCell);
                    else
                        carryCellGroup.luts().add(null);
                }
                carryCellGroups.add(carryCellGroup);
                EDIFCellGroups.get("CARRY4").remove(carryCellGroup.carry());
                EDIFCellGroups.get("FDRE").removeAll(carryCellGroup.ffs());
                EDIFCellGroups.get("LUT").removeAll(carryCellGroup.luts());

                // find the next carry cell
                EDIFHierPortInst currCellPort = currCell.getPortInst("CO[3]");
                if (currCellPort == null)
                    break;
                EDIFHierNet hnet = currCellPort.getHierarchicalNet();
                Collection<EDIFHierPortInst> netPorts = hnet.getPortInsts();
                Map<String, EDIFHierPortInst> netPortsMap = netPorts.stream()
                        .collect(Collectors.toMap(
                                p -> p.getPortInst().getName(),
                                p -> p));
                EDIFHierPortInst sinkCellPort = netPortsMap.get("CI");
                EDIFHierCellInst sinkCell = sinkCellPort.getHierarchicalInst()
                        .getChild(sinkCellPort.getPortInst().getCellInst().getName());
                currCell = sinkCell;
            } // end while(true) iterating through the chain itself
            carryCellGroupChains.add(carryCellGroups);
        } // end while() iterating over chains
        return carryCellGroupChains;
    }

    private Map<Pair<String, String>, LUTFFGroup> findLUTFFGroups(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> visitedFDREs = new ArrayList<>();
        List<EDIFHierCellInst> visitedFDSEs = new ArrayList<>();
        List<EDIFHierCellInst> visitedLUTs = new ArrayList<>();
        Map<Pair<String, String>, LUTFFGroup> groups = new HashMap<>();

        for (EDIFHierCellInst fdre : EDIFCellGroups.get("FDRE")) {
            // examine the CE net to determine which group
            EDIFHierPortInst CEPort = fdre.getPortInst("CE");
            String CENet = CEPort.getHierarchicalNet().getHierarchicalNetName();

            EDIFHierPortInst RPort = fdre.getPortInst("R");
            String RNet = RPort.getHierarchicalNet().getHierarchicalNetName();
            var enableResetPair = new Pair<String, String>(CENet, RNet);

            // examine the D net to find the LUT pair
            EDIFHierPortInst DPort = fdre.getPortInst("D");
            EDIFHierPortInst sourcePort = DPort.getHierarchicalNet().getLeafHierPortInsts(true, false).get(0);

            EDIFHierCellInst sourceCell = sourcePort.getHierarchicalInst()
                    .getChild(sourcePort.getPortInst().getCellInst().getName());

            var LUTFFPair = sourceCell.getCellType().getName().contains("LUT")
                    ? new Pair<EDIFHierCellInst, EDIFHierCellInst>(sourceCell, fdre)
                    : new Pair<EDIFHierCellInst, EDIFHierCellInst>(null, fdre);

            groups.computeIfAbsent(enableResetPair, k -> new LUTFFGroup(new ArrayList<>())).group().add(LUTFFPair);
            visitedFDREs.add(fdre);
            visitedLUTs.add(sourceCell);
        }

        for (EDIFHierCellInst fdse : EDIFCellGroups.get("FDSE")) {
            // examine the CE net to determine which group
            EDIFHierPortInst CEPort = fdse.getPortInst("CE");
            String CENet = CEPort.getHierarchicalNet().getHierarchicalNetName();

            EDIFHierPortInst SPort = fdse.getPortInst("S");
            String SNet = SPort.getHierarchicalNet().getHierarchicalNetName();
            var enableResetPair = new Pair<String, String>(CENet, SNet);

            // examine the D net to find the LUT pair
            EDIFHierPortInst DPort = fdse.getPortInst("D");
            EDIFHierPortInst sourcePort = DPort.getHierarchicalNet().getLeafHierPortInsts(true, false).get(0);

            EDIFHierCellInst sourceCell = sourcePort.getHierarchicalInst()
                    .getChild(sourcePort.getPortInst().getCellInst().getName());

            var LUTFFPair = sourceCell.getCellType().getName().contains("LUT")
                    ? new Pair<EDIFHierCellInst, EDIFHierCellInst>(sourceCell, fdse)
                    : new Pair<EDIFHierCellInst, EDIFHierCellInst>(null, fdse);

            groups.computeIfAbsent(enableResetPair, k -> new LUTFFGroup(new ArrayList<>())).group().add(LUTFFPair);
            visitedFDSEs.add(fdse);
            visitedLUTs.add(sourceCell);
        }

        EDIFCellGroups.get("FDSE").removeAll(visitedFDSEs);
        EDIFCellGroups.get("FDRE").removeAll(visitedFDREs);
        EDIFCellGroups.get("LUT").removeAll(visitedLUTs);
        return groups;
    }

    private List<List<EDIFHierCellInst>> buildLUTGroups(Map<String, List<EDIFHierCellInst>> EDIFCellGroups)
            throws IOException {
        List<List<EDIFHierCellInst>> LUTGroups = splitIntoGroups(EDIFCellGroups.get("LUT"), 4);
        for (List<EDIFHierCellInst> group : LUTGroups)
            EDIFCellGroups.get("LUT").removeAll(group);
        return LUTGroups;
    }

    private Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>> buildStackedLUTGroups(
            Map<String, List<EDIFHierCellInst>> EDIFCellGroups) throws IOException {
        List<EDIFHierCellInst> LUTCells = new ArrayList<>(EDIFCellGroups.get("LUT"));
        List<EDIFHierCellInst> LUT6Cells = new ArrayList<>();
        EDIFCellGroups.get("LUT").removeIf(c -> {
            if (c.getCellType().getName().contains("LUT6")) {
                LUT6Cells.add(c);
                return true;
            }
            return false;
        });
        EDIFCellGroups.get("LUT").removeAll(LUTCells);
        EDIFCellGroups.get("LUT").removeAll(LUT6Cells);
        List<List<EDIFHierCellInst>> LUT6Groups = splitIntoGroups(LUT6Cells, 4);
        List<List<EDIFHierCellInst>> LUTGroups = splitIntoGroups(LUTCells, 8);
        var pairLists = new Pair<List<List<EDIFHierCellInst>>, List<List<EDIFHierCellInst>>>(LUT6Groups, LUTGroups);
        return pairLists;
    }

    public static <T> List<List<T>> splitIntoGroups(List<T> list, int groupSize) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += groupSize) {
            // Create subgroups of size groupSize
            List<T> group = list.subList(i, Math.min(i + groupSize, list.size()));
            result.add(new ArrayList<>(group));
        }
        return result;
    }

    public void printEDIFCellInstList(List<EDIFCellInst> ecis) throws IOException {
        if (ecis.size() > 0) {
            String cellType = ecis.get(0).getCellType().getName();
            writer.write("\n\tPrinting all EDIFCellInsts of type " + cellType + "... (" + ecis.size() + ")");
        }
        for (EDIFCellInst eci : ecis) {
            writer.write("\n\t\t" + eci.getCellType() + ": " + eci.getName());
            Collection<EDIFPortInst> epis = eci.getPortInsts();
            for (EDIFPortInst epi : epis) {
                writer.write("\n\t\t\t" + epi.getFullName());
            }
        }
    }

    public void printCARRYChains(List<List<CarryCellGroup>> CARRYChains) throws IOException {
        writer.write("\n\nPrinting CARRYChains... (" + CARRYChains.size() + ")");
        for (List<CarryCellGroup> chain : CARRYChains) {
            writeChainDetails(chain);
        }
    }

    private void writeChainDetails(List<CarryCellGroup> chain) throws IOException {
        writer.write("\n\tAnchor Carry: " + safeGetName(chain.get(0).carry()));
        writeLutsAndFFs(chain.get(0));
        for (int i = 1; i < chain.size(); i++) {
            writer.write("\n\t\tCarry: " + safeGetName(chain.get(i).carry()));
            writeLutsAndFFs(chain.get(i));
        }
    }

    private void writeLutsAndFFs(CarryCellGroup group) throws IOException {
        for (int j = 0; j < 4; j++) {
            writer.write("\n\t\tLUT: " + safeGetName(group.luts().get(j)));
            writer.write("\n\t\t FF: " + safeGetName(group.ffs().get(j)));
        }
    }

    private String safeGetName(EDIFHierCellInst instance) {
        return instance != null ? instance.getFullHierarchicalInstName() : "Null!";
    }

    public void printDSPPairs(List<Pair<EDIFHierCellInst, EDIFHierCellInst>> DSPPairs) throws IOException {
        writer.write("\n\nPrinting DSPPairs... (" + DSPPairs.size() + ")");
        for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : DSPPairs) {
            writer.write("\n\t(" + pair.key().getCellType().getName() + ": " + pair.key().getFullHierarchicalInstName()
                    + ", " + pair.value().getCellType().getName() + ": " + pair.value().getFullHierarchicalInstName()
                    + ")");
        }
    }

    public void printLUTFFGroups(
            Map<Pair<String, String>, LUTFFGroup> LUTFFGroups)
            throws IOException {
        writer.write("\n\nPrinting Unique CE-R pairs... (" + LUTFFGroups.size() + ")");
        for (Pair<String, String> pair : LUTFFGroups.keySet()) {
            String s1 = String.format("\n\tCE: %-50s R: %-50s", pair.key(), pair.value());
            writer.write(s1);
        }
        writer.write(
                "\n\nPrinting Unique CE-R pairs with associated FF Cells... (" + LUTFFGroups.size() + ")");
        for (Map.Entry<Pair<String, String>, LUTFFGroup> entry : LUTFFGroups.entrySet()) {
            Pair<String, String> netPair = entry.getKey();
            String CENet = netPair.key();
            String RNet = netPair.value();
            LUTFFGroup lutffgroup = entry.getValue();
            writer.write(
                    "\n\tCENet: " + CENet + ", RNet: " + RNet + " with cells... (" + lutffgroup.group().size() + ")");
            for (Pair<EDIFHierCellInst, EDIFHierCellInst> pair : lutffgroup.group()) {
                if (pair.key() == null) {
                    writer.write("\n\t\tLUT: NULL!" + " => FF: "
                            + pair.value().getFullHierarchicalInstName());
                } else {
                    writer.write("\n\t\tLUT: " + pair.key().getFullHierarchicalInstName() + " => FF: "
                            + pair.value().getFullHierarchicalInstName());
                }
            }
        }
    }

    public void printLUTGroups(List<List<EDIFHierCellInst>> LUTGroups) throws IOException {
        writer.write("\n\nPrinting LUT Groups... (" + LUTGroups.size() + ")");
        for (List<EDIFHierCellInst> list : LUTGroups) {
            writer.write("\n\tGroup:");
            for (EDIFHierCellInst ehci : list) {
                writer.write("\n\t\t" + ehci.getCellType() + ": " + ehci.getFullHierarchicalInstName());
            }
        }
    }

}
