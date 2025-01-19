
`timescale 1ns/1ps

module tb_top_level
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 256,
    parameter NUM_PIPELINES = 8
)();

    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg tb_din;
    reg tb_din_valid;
    reg tb_ready;
    wire dut_ready;
    wire dut_dout;
    wire dut_dout_valid;

    localparam int SIGNAL_FREQ = 200;
    localparam int SAMPLE_FREQ = 44000;
    localparam int SAMPLES_PER_SIGNAL_PERIOD = SAMPLE_FREQ/SIGNAL_FREQ;
    localparam int ADDR_WIDTH = $clog2(SAMPLES_PER_SIGNAL_PERIOD);
    localparam CLK_PERIOD = 100; // ns

    reg [DATA_WIDTH-1:0] tb_word_in;
    reg [DATA_WIDTH-1:0] tb_word_out;
    reg [ADDR_WIDTH-1:0] tb_addr;
    int num_errors = 0;
    integer fd; // file writer

    // instantiation unit under test 
    top_level
    #(
        .DATA_WIDTH(DATA_WIDTH),
        .FIR_DEPTH(FIR_DEPTH)
    ) dut (
        .i_clk(tb_clk),
        .i_rst(tb_rst),
        .i_en(tb_en),
        .i_din(tb_din),
        .i_din_valid(tb_din_valid),
        .i_ready(tb_ready),
        .o_ready(dut_ready),
        .o_dout(dut_dout),
        .o_dout_valid(dut_dout_valid)
    );


    wire signed [DATA_WIDTH-1:0] sine_signal [SAMPLES_PER_SIGNAL_PERIOD-1:0];
    `include "sine_signal.vh"

    initial begin
        tb_clk = 1'b0;
        forever #(CLK_PERIOD/2) tb_clk = ~tb_clk;
    end

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;
        num_errors   = 0;
        tb_clk       = 1'b1;
        tb_rst       = 1'b1;
        tb_en        = 1'b0;
        tb_din       = 1'b0;
        tb_din_valid = 1'b0;
        @(posedge tb_clk);
        tb_rst = 0;
        tb_en  = 1;

        for (int t = 0; t < SAMPLES_PER_SIGNAL_PERIOD / 8; t++) begin
            tb_addr    = t;
            tb_word_in = sine_signal[tb_addr];
            @(posedge tb_clk);
            tb_din_valid = 1'b1;

            // For each bit in the sample, LSB first
            for (int j = 0; j < DATA_WIDTH; j++) begin
                // wait for deserializer ready for next bit
                wait(dut_ready == 1'b1);
                // LSB first
                tb_din = tb_word_in[j];
                @(posedge tb_clk);
            end
            // done sending 24-bit word
            tb_din_valid = 1'b0;
            // arbitrary idle wait between samples
            repeat (50) @(posedge tb_clk);
        end

        // Wait extra time at end
        repeat (1000) @(posedge tb_clk);

        $fclose(fd);
        $finish;
    end

    reg [DATA_WIDTH-1:0] tb_shift_reg;
    reg [$clog2(DATA_WIDTH+1)-1:0] tb_bit_counter;
    assign tb_ready = 1'b1;
    always @(posedge tb_clk) begin
        if (tb_rst) begin
            tb_shift_reg   <= {DATA_WIDTH{1'b0}};
            tb_bit_counter <= 0;
        end else begin
            if (dut_dout_valid) begin
                // shift in the new bit on the left side:
                tb_shift_reg   <= {dut_dout, tb_shift_reg[DATA_WIDTH-1:1]};
                tb_bit_counter <= tb_bit_counter + 1;

                // once we've collected all 24 bits, print the received word
                if (tb_bit_counter == DATA_WIDTH-1) begin
                tb_word_out = tb_shift_reg;
                $fdisplay(fd, "[%0t ns] TB DESER got word = 0x%06X", $time, tb_word_out);
                tb_bit_counter <= 0;
                end
            end
        end
    end

    initial begin
        fd = $fopen("fir_out.txt", "w");
        #10ms;
        $display("Simulation terminated after 10 milliseconds.");
        $fclose(fd);
        $finish;
    end // initial
endmodule
