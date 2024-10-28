`timescale 1ns/1ns
import math_pkg::*;

module tb_fir_filter;

    reg tb_err;
    task assert_and_report(input expected, input actual);
    begin
        if (actual == expected) begin
            $display("SUCCESS! Expected: %h, Actual: %h", expected, actual);
            tb_err = 1'b0;
        end else begin
            $display("FAILURE! Expected: %h, Actual: %h", expected, actual);
            num_errors = num_errors + 1;
            tb_err = 1'bx;
        end
    end
    endtask // assert_and_report


    localparam DATA_WIDTH = 24;
    localparam FIR_LENGTH = 128;
    int num_errors = 0;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg tb_din;
    reg [DATA_WIDTH-1:0] tb_din;
    wire [DATA_WIDTH-1:0] tb_dout;
    wire tb_prod_overflow;
    wire tb_sum_overflow;

    // instantiation unit under test 
    top_level 
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_LENGTH(FIR_LENGTH)
    ) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .i_din(tb_din),
        .o_dout(tb_dout)
    );
    always #10 tb_clk = ~tb_clk;


    real PI = 3.141615926535897;
    int SIGNAL_FREQ;
    int SAMPLE_FREQ = 44100;
    real SIGNAL_OFFS = 0;
    int SAMPLES_PER_SIGNAL_PERIOD = SAMPLE_FREQ/SIGNAL_FREQ;


    // xpm_memory_sprom: Single Port ROM
    // Xilinx Parameterized Macro, version 2024.1

    xpm_memory_sprom #(
        .ADDR_WIDTH_A(8),              // DECIMAL
        .AUTO_SLEEP_TIME(0),           // DECIMAL
        .CASCADE_HEIGHT(0),            // DECIMAL
        .ECC_BIT_RANGE("7:0"),         // String
        .ECC_MODE("no_ecc"),           // String
        .ECC_TYPE("none"),             // String
        .IGNORE_INIT_SYNTH(0),         // DECIMAL
        .MEMORY_INIT_FILE("sine.mem"),     // String
        .MEMORY_INIT_PARAM("0"),       // String
        .MEMORY_OPTIMIZATION("true"),  // String
        .MEMORY_PRIMITIVE("auto"),     // String
        .MEMORY_SIZE(SAMPLES_PER_SIGNAL_PERIOD * DATA_WIDTH),            // DECIMAL
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
        xpm_memory_sprom_inst (
        .dbiterra(dbiterra),             // 1-bit output: Leave open.
        .douta(tb_din),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Leave open.
        .addra(tb_addr),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
        .clka(tb_clk),                     // 1-bit input: Clock signal for port A.
        .ena(tb_en),                       // 1-bit input: Memory enable signal for port A. Must be high on clock
                                            // cycles when read operations are initiated. Pipelined internally.

        .injectdbiterra(injectdbiterra), // 1-bit input: Do not change from the provided value.
        .injectsbiterra(injectsbiterra), // 1-bit input: Do not change from the provided value.
        .regcea(tb_en),                 // 1-bit input: Do not change from the provided value.
        .rsta(tb_rst),                     // 1-bit input: Reset signal for the final port A output register stage.
                                            // Synchronously resets output port douta to the value specified by
                                            // parameter READ_RESET_VALUE_A.

        .sleep(sleep)                    // 1-bit input: sleep signal to enable the dynamic power saving feature.
    );

    // End of xpm_memory_sprom_inst instantiation

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;

        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;

        for (int t = 0; t < SAMPLES_PER_PERIOD), t++) begin
            tb_din = $sin(SAMPLE_FREQ * t + SIGNAL_OFFS);
        end

        $display();
        $display("Total number of errors: %d", num_errors);
        $display();

        $finish;
    end // initial
endmodule
