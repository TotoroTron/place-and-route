import sys

def main():
    # descending the layers of hell to do something that should be simple
    print("generate_xpm_spram sys.argv[0] = " + sys.argv[1])
    NUM_PIPELINES = int(sys.argv[1])
    file_path = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/"
    file_name = file_path + "generate_xpm_spram.vh"
    with open(file_name, "w") as file:

        for i in range(NUM_PIPELINES):
            file.write(
    f"""

    // xpm_memory_sprom: Single Port ROM
    // Xilinx Parameterized Macro, version 2024.1

    localparam string memfile = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/weights_{i}.mem";

    xpm_memory_sprom #(
        .ADDR_WIDTH_A(RE_ADDR_WIDTH),              // DECIMAL
        .AUTO_SLEEP_TIME(0),           // DECIMAL
        .CASCADE_HEIGHT(0),            // DECIMAL
        .ECC_BIT_RANGE("7:0"),         // String
        .ECC_MODE("no_ecc"),           // String
        .ECC_TYPE("none"),             // String
        .IGNORE_INIT_SYNTH(0),         // DECIMAL
        .MEMORY_INIT_FILE(memfile),     // String
        .MEMORY_INIT_PARAM("0"),       // String
        .MEMORY_OPTIMIZATION("false"),  // String
        .MEMORY_PRIMITIVE("ultra"),     // String
        .MEMORY_SIZE(PIPE_DEPTH * DATA_WIDTH),            // DECIMAL
        .MESSAGE_CONTROL(0),           // DECIMAL
        .RAM_DECOMP("auto"),           // String
        .READ_DATA_WIDTH_A(DATA_WIDTH),        // DECIMAL
        .READ_LATENCY_A(1),            // DECIMAL
        .READ_RESET_VALUE_A("0"),      // String
        .RST_MODE_A("SYNC"),           // String
        .SIM_ASSERT_CHK(0),            // DECIMAL; 0=disable simulation messages, 1=enable simulation messages
        .USE_MEM_INIT(1),              // DECIMAL
        .USE_MEM_INIT_MMI(0),          // DECIMAL
        .WAKEUP_TIME("disable_sleep")  // String
    )
        xpm_memory_sprom_inst_{i} (
        .dbiterra(dbiterra),             // 1-bit output: Leave open.
        .douta(weight_data[{i}]),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Leave open.
        .addra(weight_re_addr),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
        .clka(i_clk),                     // 1-bit input: Clock signal for port A.
        .ena(weight_re),                       // 1-bit input: Memory enable signal for port A. Must be high on clock
                                            // cycles when read operations are initiated. Pipelined internally.

        .injectdbiterra(1'b0), // 1-bit input: Do not change from the provided value.
        .injectsbiterra(1'b0), // 1-bit input: Do not change from the provided value.
        .regcea(i_en),                 // 1-bit input: Do not change from the provided value.
        .rsta(i_rst),                     // 1-bit input: Reset signal for the final port A output register stage.
                                            // Synchronously resets output port douta to the value specified by
                                            // parameter READ_RESET_VALUE_A.

        .sleep(1'b0)                    // 1-bit input: sleep signal to enable the dynamic power saving feature.
    );

    // End of xpm_memory_sprom_inst instantiation

    """
            ) # end file.write()
        # end for i:
    # end with open():
# end main():

if __name__ == '__main__':
    main()
