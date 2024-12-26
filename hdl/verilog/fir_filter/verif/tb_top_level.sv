
`timescale 1ps/1ps

module tb_top_level;

    localparam DATA_WIDTH = 24;
    localparam FIR_DEPTH = 256;
    localparam PIPELINES = 8;
    reg tb_clk;
    reg tb_rst;
    reg tb_en;
    reg tb_din;
    reg tb_din_valid;
    reg tb_ready;
    wire dut_ready;
    wire tb_dout;
    wire tb_dout_valid;

    localparam int SIGNAL_FREQ = 200;
    localparam int SAMPLE_FREQ = 44000;
    localparam int SAMPLES_PER_SIGNAL_PERIOD = SAMPLE_FREQ/SIGNAL_FREQ;
    localparam int ADDR_WIDTH = $clog2(SAMPLES_PER_SIGNAL_PERIOD);

    reg [DATA_WIDTH-1:0] tb_word_in;
    reg [DATA_WIDTH-1:0] serial_word;
    reg [DATA_WIDTH-1:0] tb_word_out;
    reg [ADDR_WIDTH-1:0] tb_addr;
    int num_errors = 0;
    reg tb_err;
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
        .o_dout(tb_dout),
        .o_dout_valid(tb_dout_valid)
    );


    wire signed [DATA_WIDTH-1:0] sine_signal [SAMPLES_PER_SIGNAL_PERIOD-1:0];
    `include "sine_signal.vh"

    always @(tb_addr) begin
        tb_word_in = sine_signal[tb_addr];
    end



    always #50000 tb_clk = ~tb_clk; // always 50 ns
    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;
        num_errors = 0;
        tb_clk = 1;
        tb_rst = 1;
        tb_en = 0;
        tb_din = 0;
        tb_din_valid = 0;
        tb_ready = 0;
        @(posedge tb_clk);
        // REPEAT THE SIGNAL 4 TIMES
        for (int i = 0; i < 4; i = i + 1) begin
            tb_rst = 0;
            tb_en = 1;
            // FOR EACH SAMPLE IN SIGNAL
            for (int t = 0; t < SAMPLES_PER_SIGNAL_PERIOD; t++) begin
                tb_addr = t;
                tb_din_valid = 0;
                @(posedge tb_clk);
                tb_en = 1;
                tb_rst = 0;
                tb_din_valid = 1;
                wait(dut_ready == 1'b1);
                // @(posedge tb_clk iff(dut_ready == 1'b1));
                // FOR EACH BIT IN SAMPLE
                for (int j = 0; j < DATA_WIDTH; j++) begin // LSB first
                    tb_din = tb_word_in[j];
                    @(posedge tb_clk);
                end
                tb_din_valid = 0;
                // arbitrary wait
                repeat (50) begin
                    @(posedge tb_clk);
                end
            end
        end
        repeat (1000) begin
            @(posedge tb_clk);
        end
        $fclose(fd);
        $finish;
    end // initial

    always begin
        tb_err = 0;
        @(posedge tb_clk);
        wait(tb_dout_valid == 1'b1);
        // @(posedge tb_clk iff(dut_ready == 1'b1));
        tb_ready = 1;
        @(posedge tb_clk);
        for (int i = 0; i < DATA_WIDTH; i++) begin
            serial_word[i] = tb_dout;
            @(posedge tb_clk);
        end
        tb_ready = 0;
        tb_word_out = serial_word;
        $fdisplay(fd, "%h", tb_word_out);
        tb_err = 1;
        @(posedge tb_clk);
    end

    initial begin
        fd = $fopen("fir_out.txt", "w");
        #20ms;
        $display("Simulation terminated after 20 milliseconds.");
        $fclose(fd);
        $finish;
    end // initial
endmodule
