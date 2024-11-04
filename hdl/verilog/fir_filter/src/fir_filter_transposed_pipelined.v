

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

    reg we_sample = 1'b0;
    reg re_sample = 1'b0;
    reg [ADDR_WIDTH-1:0] re_addr_sample = 0;
    reg [ADDR_WIDTH-1:0] wr_addr_sample = 0;
    wire [DATA_WIDTH-1:0] data_sample;

    reg re_weight = 1'b0;
    reg [ADDR_WIDTH-1:0] re_addr_weight = 24;
    wire [DATA_WIDTH-1:0] data_weight;

    wire [DATA_WIDTH-1:0] tap_dout;
    wire [DATA_WIDTH-1:0] sum;
    assign ov_dout = sum;


    parameter S0 = 2'b00,
        S1 = 2'b01,
        S2 = 2'b10;

    reg [1:0] state = S0;
    reg [1:0] next_state;

    // STATE REGISTER
    always @(posedge i_clk) begin
        state <= 2'bxx;
        if (i_rst)  state <= S0;
        else        state <= next_state;
    end

    // STATE MACHINE
    always @(state or i_din_valid or wr_addr_sample or re_addr_sample or re_addr_weight) begin
        // default assignments
        next_state = 2'bxx;
        o_dout_valid = 1'b0;
        we_sample = 1'b0;
        re_sample = 1'b0;
        re_weight = 1'b0;
        wr_addr_sample = wr_addr_sample;
        re_addr_sample = re_addr_sample;
        re_addr_weight = re_addr_weight;

        case (state)
        S0 : // WAIT FOR I_DIN_VALID HIGH
        begin
            if (i_din_valid) begin 
                next_state = S1;
            end else begin
                next_state = S0;
            end
        end

        S1 : // WRITE NEW SAMPLE INTO RAM
        begin
            next_state = S2;
            we_sample = 1'b1;
            if (wr_addr_sample > 0) begin
                wr_addr_sample = wr_addr_sample - 1;
            end else begin
                wr_addr_sample = FIR_DEPTH;
            end
            re_addr_sample = wr_addr_sample;
        end

        S2 : // READ SAMPLE-WEIGHT PAIRS INTO TAP ONE BY ONE
        begin
            next_state = S2;
            if (re_addr_sample < FIR_DEPTH-1) begin
                re_addr_sample = re_addr_sample + 1;
            end else begin
                re_addr_sample = 0;
            end
            if (re_addr_weight < FIR_DEPTH-1) begin
                re_addr_weight = re_addr_weight + 1;
            end else begin
                re_addr_weight = 0;
                o_dout_valid = 1'b1;
                next_state = S0;
            end
        end
        default : next_state = 2'bxx;
        endcase
    end

    tap_transposed #(
        .DATA_WIDTH(DATA_WIDTH)
    ) inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(data_sample),
        .iv_weight(data_weight),
        .iv_sum(sum),
        .ov_sum(sum),
        .ov_dout(tap_dout)
    );

    wire dbiterra, sbiterra;

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
        .douta(data_weight),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Leave open.
        .addra(re_addr_weight),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
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

    xpm_memory_spram #(
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

        .addra(wr_addr_sample),                   // ADDR_WIDTH_A-bit input: Address for port A write and read operations.
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
