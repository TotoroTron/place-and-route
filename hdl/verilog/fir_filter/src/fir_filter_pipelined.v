
module fir_filter 
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire signed [DATA_WIDTH-1:0] iv_din,
    input wire i_din_valid,
    output wire signed [DATA_WIDTH-1:0] ov_dout,
    output reg o_dout_valid
);

    wire signed [DATA_WIDTH-1:0] buffers [FIR_DEPTH-1:0];
    wire signed [DATA_WIDTH-1:0] sums [FIR_DEPTH-1:0];
    wire signed [DATA_WIDTH-1:0] weights [FIR_DEPTH-1:0];
    `include "weights.vh" 

    localparam ADDR_WIDTH = $clog2(FIR_DEPTH);

    assign ov_dout = sums[FIR_DEPTH-1];
    reg [ADDR_WIDTH-1:0] counter;

    always @(posedge i_clk) begin
        counter = counter;
        o_dout_valid = 1'b0;
        if (i_rst) begin
            counter = 0;
            o_dout_valid = 1'b0;
        end else if (i_en) begin
            if (counter < FIR_DEPTH) begin
                counter = counter + 1;
            end else begin
                counter = 0;
                o_dout_valid = 1'b1;
            end
        end
    end

    // tap #(
    //     .DATA_WIDTH(DATA_WIDTH)
    // ) inst (
    //     .i_clk(i_clk),
    //     .i_rst(i_rst),
    //     .i_en(i_en & i_din_valid),
    //     .iv_din(buffers),
    //     .iv_weight(weights),
    //     .iv_sum(sum),
    //     .ov_sum(sum),
    //     .ov_dout(buffers)
    // );

    // tap #(
    //     .DATA_WIDTH(DATA_WIDTH)
    // ) inst (
    //     .i_clk(i_clk),
    //     .i_rst(i_rst),
    //     .i_en(i_en & i_din_valid),
    //     .iv_din(iv_din),
    //     .iv_weight(weight),
    //     .iv_sum( { (DATA_WIDTH){1'b0} } ),
    //     .ov_sum(sum),
    //     .ov_dout(buffer)
    // );

    wire dbiterra, sbiterra;
    wire weight_addr, weight_data;

    // xpm_memory_sprom: Single Port ROM
    // Xilinx Parameterized Macro, version 2024.1

    xpm_memory_sprom #(
        .ADDR_WIDTH_A(ADDR_WIDTH),              // DECIMAL
        .AUTO_SLEEP_TIME(0),           // DECIMAL
        .CASCADE_HEIGHT(0),            // DECIMAL
        .ECC_BIT_RANGE("7:0"),         // String
        .ECC_MODE("no_ecc"),           // String
        .ECC_TYPE("none"),             // String
        .IGNORE_INIT_SYNTH(0),         // DECIMAL
        .MEMORY_INIT_FILE("/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/weights.mem"),     // String
        .MEMORY_INIT_PARAM("0"),       // String
        .MEMORY_OPTIMIZATION("true"),  // String
        .MEMORY_PRIMITIVE("auto"),     // String
        .MEMORY_SIZE(FIR_DEPTH * DATA_WIDTH),            // DECIMAL
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
        .douta(weight_data),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Leave open.
        .addra(weight_addr),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
        .clka(i_clk),                     // 1-bit input: Clock signal for port A.
        .ena(i_en),                       // 1-bit input: Memory enable signal for port A. Must be high on clock
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

endmodule
