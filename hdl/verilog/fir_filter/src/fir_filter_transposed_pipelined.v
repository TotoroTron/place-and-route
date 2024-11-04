

module fir_filter_transposed_pipelined
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
    output reg o_ready,
    output reg o_dout_valid
);

    localparam ADDR_WIDTH = $clog2(FIR_DEPTH);

    wire we_sample;
    wire re_sample;
    wire unsigned [ADDR_WIDTH-1:0] addr_sample;
    wire [DATA_WIDTH-1:0] data_sample;

    wire re_weight;
    wire unsigned [ADDR_WIDTH-1:0] addr_weight;
    wire [DATA_WIDTH-1:0] data_weight;

    reg [DATA_WIDTH-1:0] accumulator;
    assign ov_dout = accumulator;


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

    WEIGHTS_ROM : xpm_memory_sprom #(
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
        .douta(data_weight),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Leave open.
        .addra(addr_weight),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
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


    // xpm_memory_spram: Single Port RAM
    // Xilinx Parameterized Macro, version 2024.1

    SAMPLES_RAM : xpm_memory_spram #(
        .ADDR_WIDTH_A(ADDR_WIDTH),              // DECIMAL
        .AUTO_SLEEP_TIME(0),           // DECIMAL
        .BYTE_WRITE_WIDTH_A(DATA_WIDTH),       // DECIMAL
        .CASCADE_HEIGHT(0),            // DECIMAL
        .ECC_BIT_RANGE("7:0"),         // String
        .ECC_MODE("no_ecc"),           // String
        .ECC_TYPE("none"),             // String
        .IGNORE_INIT_SYNTH(0),         // DECIMAL
        .MEMORY_INIT_FILE("none"),     // String
        .MEMORY_INIT_PARAM("0"),       // String
        .MEMORY_OPTIMIZATION("true"),  // String
        .MEMORY_PRIMITIVE("auto"),     // String
        .MEMORY_SIZE(FIR_DEPTH * DATA_WIDTH),            // DECIMAL
        .MESSAGE_CONTROL(0),           // DECIMAL

        .RAM_DECOMP("auto"),           // String
        .READ_DATA_WIDTH_A(DATA_WIDTH),        // DECIMAL
        .READ_LATENCY_A(2),            // DECIMAL
        .READ_RESET_VALUE_A("0"),      // String
        .RST_MODE_A("SYNC"),           // String
        .SIM_ASSERT_CHK(0),            // DECIMAL; 0=disable simulation messages, 1=enable simulation messages
        .USE_MEM_INIT(1),              // DECIMAL
        .USE_MEM_INIT_MMI(0),          // DECIMAL
        .WAKEUP_TIME("disable_sleep"), // String
        .WRITE_DATA_WIDTH_A(DATA_WIDTH),       // DECIMAL
        .WRITE_MODE_A("read_first"),   // String
        .WRITE_PROTECT(1)              // DECIMAL
    )
    xpm_memory_spram_inst (
        .dbiterra(dbiterra),             // 1-bit output: Status signal to indicate double bit error occurrence
                                            // on the data output of port A.

        .douta(data_sample),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Status signal to indicate single bit error occurrence
                                            // on the data output of port A.

        .addra(addr_sample),                   // ADDR_WIDTH_A-bit input: Address for port A write and read operations.
        .clka(i_clk),                     // 1-bit input: Clock signal for port A.
        .dina(iv_din),                     // WRITE_DATA_WIDTH_A-bit input: Data input for port A write operations.
        .ena(i_en),                       // 1-bit input: Memory enable signal for port A. Must be high on clock
                                            // cycles when read or write operations are initiated. Pipelined
                                            // internally.

        .injectdbiterra(1'b0), // 1-bit input: Controls double bit error injection on input data when
                                            // ECC enabled (Error injection capability is not available in
                                            // "decode_only" mode).

        .injectsbiterra(1'b0), // 1-bit input: Controls single bit error injection on input data when
                                            // ECC enabled (Error injection capability is not available in
                                            // "decode_only" mode).

        .regcea(i_en),                 // 1-bit input: Clock Enable for the last register stage on the output
                                            // data path.

        .rsta(i_rst),                     // 1-bit input: Reset signal for the final port A output register stage.
                                            // Synchronously resets output port douta to the value specified by
                                            // parameter READ_RESET_VALUE_A.

        .sleep(1'b0),                   // 1-bit input: sleep signal to enable the dynamic power saving feature.
        .wea(we_sample)                        // WRITE_DATA_WIDTH_A/BYTE_WRITE_WIDTH_A-bit input: Write enable vector
                                            // for port A input data port dina. 1 bit wide when word-wide writes are
                                            // used. In byte-wide write configurations, each bit controls the
                                            // writing one byte of dina to address addra. For example, to
                                            // synchronously write only bits [15-8] of dina when WRITE_DATA_WIDTH_A
                                            // is 32, wea would be 4'b0010.

    );

    // End of xpm_memory_spram_inst instantiation

endmodule
