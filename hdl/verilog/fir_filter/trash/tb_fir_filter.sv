// module tb_fir_filter;
// 
//     localparam DATA_WIDTH = 24;
//     localparam FIR_DEPTH = 8;
//     int num_errors = 0;
//     reg tb_clk;
//     reg tb_rst;
//     reg tb_en;
//     reg [DATA_WIDTH-1:0] tb_din;
//     wire [DATA_WIDTH-1:0] tb_dout;
//     
//     reg [23:0] tb_addr;
//     wire tb_prod_overflow;
//     wire tb_sum_overflow;
//     reg tb_err;
// 
//     task assert_and_report(input expected, input actual);
//     begin
//         if (actual == expected) begin
//             $display("SUCCESS! Expected: %h, Actual: %h", expected, actual);
//             tb_err = 1'b0;
//         end else begin
//             $display("FAILURE! Expected: %h, Actual: %h", expected, actual);
//             num_errors = num_errors + 1;
//             tb_err = 1'bx;
//         end
//     end
//     endtask // assert_and_report
// 
// 
//     // instantiation unit under test 
//     fir_filter 
//     #(
//         .DATA_WIDTH(DATA_WIDTH),
//         .FIR_DEPTH(FIR_DEPTH)
//     ) dut (
//         .i_clk(tb_clk),
//         .i_rst(tb_rst),
//         .i_en(tb_en),
//         .iv_din(tb_din),
//         .ov_dout(tb_dout)
//     );
// 
// 
//     localparam int SIGNAL_FREQ = 100;
//     localparam int SAMPLE_FREQ = 44000;
//     localparam int SAMPLES_PER_SIGNAL_PERIOD = SAMPLE_FREQ/SIGNAL_FREQ;
// 
// 
//     wire tb_dbiterra, tb_sbiterra;
// 
//     // xpm_memory_sprom: Single Port ROM
//     // Xilinx Parameterized Macro, version 2024.1
// 
//     xpm_memory_sprom #(
//         .ADDR_WIDTH_A(24),              // DECIMAL
//         .AUTO_SLEEP_TIME(0),           // DECIMAL
//         .CASCADE_HEIGHT(0),            // DECIMAL
//         .ECC_BIT_RANGE("7:0"),         // String
//         .ECC_MODE("no_ecc"),           // String
//         .ECC_TYPE("none"),             // String
//         .IGNORE_INIT_SYNTH(0),         // DECIMAL
//         .MEMORY_INIT_FILE("/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/verif/sine.mem"),     // String
//         .MEMORY_INIT_PARAM("0"),       // String
//         .MEMORY_OPTIMIZATION("true"),  // String
//         .MEMORY_PRIMITIVE("auto"),     // String
//         .MEMORY_SIZE(SAMPLES_PER_SIGNAL_PERIOD * DATA_WIDTH),            // DECIMAL
//         .MESSAGE_CONTROL(0),           // DECIMAL
//         .RAM_DECOMP("auto"),           // String
//         .READ_DATA_WIDTH_A(DATA_WIDTH),        // DECIMAL
//         .READ_LATENCY_A(1),            // DECIMAL
//         .READ_RESET_VALUE_A("0"),      // String
//         .RST_MODE_A("SYNC"),           // String
//         .SIM_ASSERT_CHK(0),            // DECIMAL; 0=disable simulation messages, 1=enable simulation messages
//         .USE_MEM_INIT(1),              // DECIMAL
//         .USE_MEM_INIT_MMI(0),          // DECIMAL
//         .WAKEUP_TIME("disable_sleep")  // String
//     )
//         xpm_memory_sprom_inst (
//         .dbiterra(tb_dbiterra),             // 1-bit output: Leave open.
//         .douta(tb_din),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
//         .sbiterra(tb_sbiterra),             // 1-bit output: Leave open.
//         .addra(tb_addr),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
//         .clka(tb_clk),                     // 1-bit input: Clock signal for port A.
//         .ena(tb_en),                       // 1-bit input: Memory enable signal for port A. Must be high on clock
//                                             // cycles when read operations are initiated. Pipelined internally.
// 
//         .injectdbiterra(1'b0), // 1-bit input: Do not change from the provided value.
//         .injectsbiterra(1'b0), // 1-bit input: Do not change from the provided value.
//         .regcea(tb_en),                 // 1-bit input: Do not change from the provided value.
//         .rsta(tb_rst),                     // 1-bit input: Reset signal for the final port A output register stage.
//                                             // Synchronously resets output port douta to the value specified by
//                                             // parameter READ_RESET_VALUE_A.
// 
//         .sleep(1'b0)                    // 1-bit input: sleep signal to enable the dynamic power saving feature.
//     );
// 
// 
//     // End of xpm_memory_sprom_inst instantiation
// 
//     always #5 tb_clk = ~tb_clk;
// 
//     initial begin
//         $dumpfile("waveform.vcd");
//         $dumpvars;
// 
//         num_errors = 0;
//         tb_clk = 1;
//         tb_rst = 1;
// 
//         for (int i = 0; i < 2; i = i + 1) begin
//             tb_rst = 0;
//             tb_en = 1;
//             for (int t = 0; t < SAMPLES_PER_SIGNAL_PERIOD; t++) begin
//                 tb_addr = t;
//                 @(posedge tb_clk);
//             end
//         end
// 
//         $display();
//         $display("Total number of errors: %d", num_errors);
//         $display();
// 
//         $finish;
//     end // initial
// endmodule
