
`timescale 1ns/1ps

module tb_top_level
#(
    parameter DATA_WIDTH = 24,
    parameter FIR_DEPTH = 256,
    parameter NUM_PIPELINES = 8
)();

    logic tb_clk;
    logic tb_rst;
    logic tb_en;
    logic tb_din;
    logic tb_din_valid;
    logic tb_ready;
    logic dut_ready;
    logic dut_dout;
    logic dut_dout_valid;

    localparam int SIGNAL_FREQ = 200;
    localparam int SAMPLE_FREQ = 44000;
    localparam int SAMPLES_PER_SIGNAL_PERIOD = SAMPLE_FREQ/SIGNAL_FREQ;
    localparam int ADDR_WIDTH = $clog2(SAMPLES_PER_SIGNAL_PERIOD);
    localparam CLK_PERIOD = 100; // ns

    logic [DATA_WIDTH-1:0] tb_word_in;
    logic [DATA_WIDTH-1:0] tb_word_out;
    logic [ADDR_WIDTH-1:0] tb_addr;
    integer fd_des; // file writer
    integer fd_ser; // file writer
    integer error_count_des = 0;
    integer transactions_des = 0;
    integer error_count_ser = 0;
    integer transactions_ser = 0;

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


    logic signed [DATA_WIDTH-1:0] sine_signal [SAMPLES_PER_SIGNAL_PERIOD-1:0];
    `include "sine_signal.vh"

    initial begin
        tb_clk = 1'b0;
        forever #(CLK_PERIOD/2) tb_clk = ~tb_clk;
    end

    initial begin
        $dumpfile("waveform.vcd");
        $dumpvars;
        tb_clk       = 1'b1;
        tb_rst       = 1'b1;
        tb_en        = 1'b0;
        tb_din       = 1'b0;
        tb_din_valid = 1'b0;
        @(posedge tb_clk);
        tb_rst = 0;
        tb_en  = 1;

        repeat (1) begin
            for (int t = 0; t < SAMPLES_PER_SIGNAL_PERIOD; t++) begin
                tb_addr    = t;
                tb_word_in = sine_signal[tb_addr];
                @(posedge tb_clk);
                tb_din_valid = 1'b1;
                // For each bit in the sample, LSB first
                for (int j = 0; j < DATA_WIDTH; j++) begin
                    tb_din = tb_word_in[j];
                    wait(dut_ready == 1'b1);
                    @(posedge tb_clk);
                end
                // done sending 24-bit word
                tb_din_valid = 1'b0;

                transactions_des = transactions_des + 1;
                @(posedge tb_clk);
                if (tb_word_in == top_level.fir_din) begin
                    $fdisplay(fd_des, "Success!");
                end else begin
                    $fdisplay(fd_des, "FAILURE!");
                    error_count_des = error_count_des + 1;
                end
                $fdisplay(fd_des,
                    "\t[%4t ns]   TB sent:  0b%024b",
                    $time, tb_word_in
                );
                $fdisplay(fd_des,
                    "\t[%4t ns]  FIR rcvd:  0b%024b",
                    $time, top_level.fir_din
                );
                // arbitrary idle wait between samples
                repeat (50) @(posedge tb_clk);
            end
        end

        // Wait extra time at end
        repeat (1000) @(posedge tb_clk);

        $display("Deserializer errors: (%6d) out of (%6d)", error_count_des, transactions_des);
        $display("Serializezr errors: (%6d) out of (%6d)", error_count_ser, transactions_ser);
        $fdisplay(fd_des, "Deserializer errors: (%6d) out of (%6d)", error_count_des, transactions_des);
        $fdisplay(fd_ser, "Serializer errors: (%6d) out of (%6d)", error_count_ser, transactions_ser);
        $fclose(fd_des);
        $fclose(fd_ser);
        $finish;
    end

    logic [DATA_WIDTH-1:0] tb_shift_reg;

    always begin
        tb_ready = 1'b0;
        @(posedge tb_clk);
        wait(dut_dout_valid == 1'b0);
        wait(dut_dout_valid == 1'b1);
        @(posedge tb_clk);
        @(negedge tb_clk);
        tb_ready = 1'b1;
        repeat(DATA_WIDTH) begin
            tb_shift_reg = {dut_dout, tb_shift_reg[DATA_WIDTH-1:1]};
            @(negedge tb_clk);
        end
        @(negedge tb_clk);
        tb_ready = 1'b0;
        tb_word_out = tb_shift_reg;
        transactions_ser = transactions_ser + 1;

        if (top_level.fir_dout == tb_word_out) begin
            $fdisplay(fd_ser, "Success!");
        end else begin
            $fdisplay(fd_ser, "FAILURE!");
            error_count_ser = error_count_ser + 1;
        end

        $fdisplay(fd_ser,
            "\t[%4t ns]  FIR sent:  0b%024b",
            $time, top_level.fir_dout
        );
        $fdisplay(fd_ser,
            "\t[%4t ns]   TB rcvd:  0b%024b",
            $time, tb_word_out
        );
        
        repeat (50) begin
            @(posedge tb_clk);
        end
    end

    initial begin
        fd_des = $fopen("deserializer.txt", "w");
        fd_ser = $fopen("serializer.txt", "w");
        #10ms;
        $display("Simulation terminated after 10 milliseconds.");
        $display("Deserializer errors: (%6d) out of (%6d)", error_count_des, transactions_des);
        $display("Serializezr errors: (%6d) out of (%6d)", error_count_ser, transactions_ser);
        $fdisplay(fd_des, "Deserializer errors: (%6d) out of (%6d)", error_count_des, transactions_des);
        $fdisplay(fd_ser, "Serializer errors: (%6d) out of (%6d)", error_count_ser, transactions_ser);
        $fclose(fd_des);
        $fclose(fd_ser);
        $finish;
    end // initial
endmodule
