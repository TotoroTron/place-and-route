

module tb_top_level;

    localparam DATA_WIDTH = 24;
    localparam FIR_DEPTH = 32;
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

    wire tb_dbiterra, tb_sbiterra;

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
        .MEMORY_INIT_FILE("/home/bcheng/workspace/dev/place-and-route/hdl/verilog/fir_filter/verif/sine.mem"),     // String
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
        .dbiterra(tb_dbiterra),             // 1-bit output: Leave open.
        .douta(tb_word_in),                   // READ_DATA_WIDTH_A-bit output: Data output for port A read operations.
        .sbiterra(tb_sbiterra),             // 1-bit output: Leave open.
        .addra(tb_addr),                   // ADDR_WIDTH_A-bit input: Address for port A read operations.
        .clka(tb_clk),                     // 1-bit input: Clock signal for port A.
        .ena(tb_en),                       // 1-bit input: Memory enable signal for port A. Must be high on clock
                                            // cycles when read operations are initiated. Pipelined internally.

        .injectdbiterra(1'b0), // 1-bit input: Do not change from the provided value.
        .injectsbiterra(1'b0), // 1-bit input: Do not change from the provided value.
        .regcea(tb_en),                 // 1-bit input: Do not change from the provided value.
        .rsta(tb_rst),                     // 1-bit input: Reset signal for the final port A output register stage.
                                            // Synchronously resets output port douta to the value specified by
                                            // parameter READ_RESET_VALUE_A.

        .sleep(1'b0)                    // 1-bit input: sleep signal to enable the dynamic power saving feature.
    );


    // End of xpm_memory_sprom_inst instantiation


    always #5 tb_clk = ~tb_clk;
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

                wait(dut_ready == 1'b1); // waits for dut to signal ready
                @(posedge tb_clk);

                // FOR EACH BIT IN SAMPLE
                for (int j = 0; j < DATA_WIDTH; j++) begin
                    // serialize word, LSB first in
                    tb_din = tb_word_in[j];
                    // if (j == DATA_WIDTH-1) begin
                    //     tb_din_valid = 1;
                    // end
                    @(posedge tb_clk);
                end

                tb_din_valid = 0;

                // arbitrary wait
                for (int i = 0; i < 50; i++) begin
                    @(posedge tb_clk);
                end
            end
        end


        $finish;
    end // initial

    always begin
        wait(tb_dout_valid == 1'b1); // waits for dout valid from dut
        @(posedge tb_clk);
        tb_ready = 1;
        for (int i = 0; i < DATA_WIDTH; i++) begin
            serial_word[i] = tb_dout;
            @(posedge tb_clk);
        end
        @(posedge tb_clk);
        tb_ready = 0;
        tb_word_out = serial_word;
        @(posedge tb_clk);
    end

    initial begin
        #50ms; // Wait for 1ms simulation time
        $display("Simulation terminated after 50 milliseconds.");
        $finish;
    end // initial
endmodule
