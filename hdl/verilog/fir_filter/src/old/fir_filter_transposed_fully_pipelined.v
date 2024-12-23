

module fir_filter_transposed_fully_pipelined
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 16
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire signed [DATA_WIDTH-1:0] iv_din,
    input wire i_din_valid,
    input wire i_ready,
    output reg o_ready,
    output reg [DATA_WIDTH-1:0] ov_dout,
    output reg o_dout_valid
);

    localparam ADDR_WIDTH = $clog2(FIR_DEPTH)+1;

    reg sample_we = 1'b0;
    reg sample_re = 1'b0;
    wire [DATA_WIDTH-1:0] sample_data;
    reg [ADDR_WIDTH-1:0] sample_re_addr= 0;
    reg [ADDR_WIDTH-1:0] sample_wr_addr= 0;
    reg [ADDR_WIDTH-1:0] sample_addr= 0;

    reg weight_re = 1'b0;
    reg [ADDR_WIDTH-1:0] weight_re_addr = 0;
    wire [DATA_WIDTH-1:0] weight_data;

    wire [DATA_WIDTH-1:0] tap_dout;
    wire [DATA_WIDTH-1:0] sum;
    reg sum_rst;

    parameter S0 = 4'b0000,
        S1 = 4'b0001,
        S2 = 4'b0010,
        S3 = 4'b0011;
    reg [3:0] state = S0;
    reg [3:0] next_state;


    // STATE REGISTER
    always @(posedge i_clk) begin
        if (i_rst)  state <= S0;
        else        state <= next_state;
    end

    // STATE MACHINE
    always @(*) begin
        case (state)
            S0: begin
                next_state <= S0;
                // WAIT FOR DIN VALID
                if (i_din_valid)
                    next_state <= S1;
            end
            S1: begin
                // SIGNAL DATA CONSUMED
                // WRITE SAMPLE INTO RAM
                next_state <= S2;
            end
            S2: begin
                next_state <= S2;
                // PIPELINED MAC
                // ASSERT OUTPUT DATA VALID WHEN FINISHED
                if (weight_re_addr == FIR_DEPTH-1)
                    next_state <= S3;
            end
            S3: begin
                next_state <= S3;
                // WAIT FOR RECEIVER TO CONSUME OUTPUT DATA
                if (i_ready)
                    next_state <= S0;
            end
            default: next_state <= S0;
        endcase
    end

    // OUTPUT LOGIC
    always @(posedge i_clk) begin
        if (i_rst) begin
            o_ready = 1'b0;
            o_dout_valid = 1'b0;
            sample_we = 1'b0;
            sample_re = 1'b0;
            weight_re = 1'b0;
            sample_wr_addr = 0;
            sample_re_addr = 0;
            weight_re_addr = 0;
            sample_addr = 0;

        end else begin
            // Default assignments
            o_ready = 1'b0;
            sample_we = 1'b0;
            sample_re = 1'b0;
            weight_re = 1'b0;
            sum_rst = 1'b0;
            case (state)
                S0: begin
                    // WAIT FOR INPUT DATA VALID
                    sum_rst = 1'b1;
                end

                S1: begin
                    // SIGNAL DATA CONSUMED
                    // WRITE SAMPLE INTO RAM
                    o_ready = 1'b1;
                    sample_we = 1'b1;
                    if (sample_wr_addr > 0)
                        sample_wr_addr = sample_wr_addr - 1;
                    else
                        sample_wr_addr = FIR_DEPTH - 1;
                    sample_re_addr = sample_wr_addr;
                    sample_addr = sample_wr_addr;
                end

                S2: begin
                    // PIPELINED MAC
                    weight_re = 1'b1;
                    sample_re = 1'b1;
                    if (sample_re_addr < FIR_DEPTH - 1)
                        sample_re_addr = sample_re_addr + 1;
                    else
                        sample_re_addr = 0;
                    if (weight_re_addr < FIR_DEPTH - 1)
                        weight_re_addr = weight_re_addr + 1;
                    else begin
                        weight_re_addr = 0;
                        o_dout_valid = 1'b1;
                        ov_dout = sum;
                    end
                    sample_addr = sample_re_addr;
                end

                S3: begin
                    // WAIT FOR RECEIVER TO CONSUME OUTPUT DATA
                end

                default: begin
                    o_ready = 1'b0;
                    o_dout_valid = 1'b0;
                end

            endcase
        end
    end

    tap_transposed #(
        .DATA_WIDTH(DATA_WIDTH)
    ) tap_inst (
        .i_clk(i_clk),
        .i_rst(i_rst || sum_rst),
        .i_en(i_en),
        .iv_din(sample_data),
        .iv_weight(weight_data),
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
        .MEMORY_INIT_FILE("/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/weights0.mem"),     // String
        .MEMORY_INIT_PARAM("0"),       // String
        .MEMORY_OPTIMIZATION("false"),  // String
        .MEMORY_PRIMITIVE("ultra"),     // String
        // .MEMORY_SIZE(FIR_DEPTH * DATA_WIDTH),            // DECIMAL
        .MEMORY_SIZE((2**ADDR_WIDTH) * DATA_WIDTH),            // DECIMAL
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
        .MEMORY_OPTIMIZATION("false"),  // String
        .MEMORY_PRIMITIVE("ultra"),     // String
        // .MEMORY_SIZE(FIR_DEPTH * DATA_WIDTH),            // DECIMAL
        .MEMORY_SIZE((2**ADDR_WIDTH) * DATA_WIDTH),            // DECIMAL
        .MESSAGE_CONTROL(0),           // DECIMAL

        .RAM_DECOMP("auto"),           // String
        .READ_DATA_WIDTH_A(DATA_WIDTH),        // DECIMAL
        .READ_LATENCY_A(1),            // DECIMAL
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

        .douta(sample_data),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(sbiterra),             // 1-bit output: Status signal to indicate single bit error occurrence
                                            // on the data output of port A.

        .addra(sample_addr),                   // ADDR_WIDTH_A-bit input: Address for port A write and read operations.
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
        .wea(sample_we)                        // WRITE_DATA_WIDTH_A/BYTE_WRITE_WIDTH_A-bit input: Write enable vector
                                            // for port A input data port dina. 1 bit wide when word-wide writes are
                                            // used. In byte-wide write configurations, each bit controls the
                                            // writing one byte of dina to address addra. For example, to
                                            // synchronously write only bits [15-8] of dina when WRITE_DATA_WIDTH_A
                                            // is 32, wea would be 4'b0010.

    );

    // End of xpm_memory_spram_inst instantiation

endmodule
