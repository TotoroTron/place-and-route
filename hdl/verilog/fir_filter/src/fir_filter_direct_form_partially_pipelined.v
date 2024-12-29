
module fir_filter_direct_form_partially_pipelined
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 16,
    parameter PIPELINES = 1
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

    localparam WR_ADDR_WIDTH = $clog2(FIR_DEPTH);
    localparam PIPE_DEPTH = FIR_DEPTH / PIPELINES;
    localparam RE_ADDR_WIDTH = $clog2(PIPE_DEPTH);
    localparam WR_SEL_WIDTH = WR_ADDR_WIDTH - RE_ADDR_WIDTH;

    // reg [WR_SEL_WIDTH-1:0] wr_sel;

    reg sample_we = 1'b0;
    reg sample_re = 1'b0;
    wire [DATA_WIDTH-1:0] sample_data [PIPELINES-1:0];
    reg [RE_ADDR_WIDTH-1:0] sample_re_addr = 0;
    reg [WR_ADDR_WIDTH-1:0] sample_wr_addr = 0;
    reg [WR_ADDR_WIDTH-1:0] sample_addr = 0;

    reg weight_re = 1'b0;
    reg [RE_ADDR_WIDTH-1:0] weight_re_addr = 0;
    wire [DATA_WIDTH-1:0] weight_data [PIPELINES-1:0];

    reg tap_en;
    wire [DATA_WIDTH-1:0] tap_dout [PIPELINES-1:0];
    wire [DATA_WIDTH-1:0] part_sum [PIPELINES-1:0];
    reg [DATA_WIDTH-1:0] acc;
    reg sum_rst;

    parameter WAIT_DIN_VALID = 4'b0000,
        WRITE_DIN_SAMPLE = 4'b0001,
        SHIFT_SAMPLES = 4'b0010,
        PROCESS_SAMPLE = 4'b0011,
        WAIT_DOUT_READY = 4'b0100;
    reg [3:0] state = WAIT_DIN_VALID;
    reg [3:0] next_state;


    // STATE REGISTER
    always @(posedge i_clk) begin
        if (i_rst)  state <= WAIT_DIN_VALID;
        else        state <= next_state;
    end

    // STATE MACHINE
    always @(*) begin
        case (state)
            WAIT_DIN_VALID: begin
                next_state <= WAIT_DIN_VALID;
                // WAIT FOR DIN VALID
                if (i_din_valid)
                    next_state <= WRITE_DIN_SAMPLE;
            end
            WRITE_DIN_SAMPLE: begin
                // SIGNAL DATA CONSUMED
                // WRITE SAMPLE INTO RAM
                next_state <= SHIFT_SAMPLES;
            end
            SHIFT_SAMPLES: begin
                next_state <= PROCESS_SAMPLE;
            end
            PROCESS_SAMPLE: begin
                next_state <= PROCESS_SAMPLE;
                // PIPELINED MAC
                // ASSERT OUTPUT DATA VALID WHEN FINISHED
                if (weight_re_addr == PIPE_DEPTH-1)
                    next_state <= WAIT_DOUT_READY;
            end
            WAIT_DOUT_READY: begin
                next_state <= WAIT_DOUT_READY;
                // WAIT FOR RECEIVER TO CONSUME OUTPUT DATA
                if (i_ready)
                    next_state <= WAIT_DIN_VALID;
            end
            default: next_state <= WAIT_DIN_VALID;
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
            tap_en = 1'b0;
            sum_rst = 1'b0;
            case (state)
                WAIT_DIN_VALID: begin
                    // WAIT FOR INPUT DATA VALID
                    sum_rst = 1'b1;
                end

                SHIFT_SAMPLES: begin
                    tap_en = 1'b0;
                    sample_re = 1'b1;
                    sample_re_addr = sample_wr_addr;
                end

                WRITE_DIN_SAMPLE: begin
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

                PROCESS_SAMPLE: begin
                    // PIPELINED MAC
                    weight_re = 1'b1;
                    sample_re = 1'b1;
                    tap_en = 1'b1;
                    if (sample_re_addr < PIPE_DEPTH - 1)
                        sample_re_addr = sample_re_addr + 1;
                    else
                        sample_re_addr = 0;
                    if (weight_re_addr < PIPE_DEPTH - 1)
                        weight_re_addr = weight_re_addr + 1;
                    else begin
                        weight_re_addr = 0;
                        o_dout_valid = 1'b1;
                        ov_dout = acc;
                    end
                    sample_addr = sample_re_addr;
                end

                WAIT_DOUT_READY: begin
                    // WAIT FOR RECEIVER TO CONSUME OUTPUT DATA
                end

                default: begin
                    o_ready = 1'b0;
                    o_dout_valid = 1'b0;
                end

            endcase
        end
    end


    genvar i;
    generate
        for (i = 0; i < PIPELINES; i = i + 1) begin
            tap_transposed #(
                .DATA_WIDTH(DATA_WIDTH)
            ) tap_inst (
                .i_clk(i_clk),
                .i_rst(i_rst || sum_rst),
                .i_en(tap_en),
                .iv_din(sample_data[i]),
                .iv_weight(weight_data[i]),
                .iv_sum(part_sum[i]),
                .ov_sum(part_sum[i]),
                .ov_dout(tap_dout[i])
            );
        end
    endgenerate

    integer k;
    always @(*) begin
        acc = 0;
        for (k = 0; k < PIPELINES; k = k + 1) begin
            acc = acc + part_sum[k];
        end
    end

    wire sbiterra, dbiterra;

    // `include "generate_xpm_spram.vh"

    generate
    for (i = 0; i < PIPELINES; i = i + 1) begin

        // https://stackoverflow.com/questions/58833613/how-to-generate-a-string-from-a-genvar-value-in-a-for-loop
        // https://stackoverflow.com/questions/70439991/how-to-display-string-on-verilog

        // localparam [8*70-1:0] src_dir = "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/";
        // weig hts_ 00.m em
        // 1234 5678 9ABC DE => 14 ASCII characters
        // localparam [8*14-1:0] file_name = {"weights_", (i/10) + 8'h30, (i%10) + 8'h30, ".mem"};
        // localparam [8*88-1:0] full_name = {src_dir, file_name};

        // i dont know how xelab works, but
        // [0:8*90-1] is more bits than required to store full_name ASCII
        // there must be some spooky padding handling going on with the tool.
        // in fact, [0:8*200-1] also yields the correct full_name for xpm
        // component instantiation.
        // so when in doubt, just overshoot the amount of bits required to
        // store the string.
        // [0:8*90-1] is precisely the lowest number of bits to store the
        // string correctly after guess-and-check experimenation.

        localparam [0:8*90-1] full_name = {
            "/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/src/",
            "weights_",
            (i/10) + 8'h30,
            (i%10) + 8'h30,
            ".mem"
        };
        // i am vomit


        // xpm_memory_sprom: Single Port ROM
        // Xilinx Parameterized Macro, version 2024.1

        xpm_memory_sprom #(
            .ADDR_WIDTH_A(RE_ADDR_WIDTH),              // DECIMAL
            .AUTO_SLEEP_TIME(0),           // DECIMAL
            .CASCADE_HEIGHT(0),            // DECIMAL
            .ECC_BIT_RANGE("7:0"),         // String
            .ECC_MODE("no_ecc"),           // String
            .ECC_TYPE("none"),             // String
            .IGNORE_INIT_SYNTH(0),         // DECIMAL
            .MEMORY_INIT_FILE( full_name ),     // String
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
            xpm_memory_sprom_inst (
            .dbiterra(dbiterra),             // 1-bit output: Leave open.
            .douta(weight_data[i]),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
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
    end
    endgenerate

    wire [DATA_WIDTH-1:0] spram_din [PIPELINES-1:0];
    generate
    for (i = 0; i < PIPELINES; i = i + 1) begin
        assign spram_din[i] = (i == 0) ? iv_din : sample_data[i-1];
        // xpm_memory_spram: Single Port RAM
        // Xilinx Parameterized Macro, version 2024.1

        xpm_memory_spram #(
            .ADDR_WIDTH_A(RE_ADDR_WIDTH),              // DECIMAL
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
            .WAKEUP_TIME("disable_sleep"), // String
            .WRITE_DATA_WIDTH_A(DATA_WIDTH),       // DECIMAL
            .WRITE_MODE_A("read_first"),   // String
            .WRITE_PROTECT(1)              // DECIMAL
        )
        xpm_memory_spram_inst (
            .dbiterra(dbiterra),             // 1-bit output: Status signal to indicate double bit error occurrence
                                                // on the data output of port A.

            .douta(sample_data[i]),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
            .sbiterra(sbiterra),             // 1-bit output: Status signal to indicate single bit error occurrence
                                                // on the data output of port A.

            .addra(sample_addr),                   // ADDR_WIDTH_A-bit input: Address for port A write and read operations.
            .clka(i_clk),                     // 1-bit input: Clock signal for port A.
            .dina(spram_din[i]),                     // WRITE_DATA_WIDTH_A-bit input: Data input for port A write operations.
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
    end
    endgenerate

endmodule
