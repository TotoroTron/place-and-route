module top_level
#(parameter DATA_WIDTH = 16)
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_en,
        input wire i_din_a,
        input wire i_din_b,
        input wire i_valid,
        input wire i_ready, // from tb rx
        output wire o_ready, // to tb tx
        output wire o_prod,
        output wire o_valid
    );

    wire des_i_ready_a; // from ser
    wire des_o_ready_a; // to tb
    wire des_o_valid_a;
    wire [DATA_WIDTH-1:0] des_dout_a;

    wire des_i_ready_b; // from ser
    wire des_o_ready_b; // to tb
    wire des_o_valid_b;
    wire [DATA_WIDTH-1:0] des_dout_b;


    wire [DATA_WIDTH*2-1:0] prod;
    reg des_o_valid;
    wire ser_ready;

    assign des_i_ready_a = i_ready;
    assign des_i_ready_b = i_ready;
    assign o_ready = des_o_ready_a & des_o_ready_b;

    deserializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) deserializer_inst_a (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .i_din(i_din_a),
        .i_din_valid(i_valid),
        .i_ready(ser_ready),
        .o_ready(des_o_ready_a),
        .ov_dout(des_dout_a),
        .o_dout_valid(des_o_valid_a)
    );

    deserializer_fsm
    #(
        .LENGTH(DATA_WIDTH)
    ) deserializer_inst_b (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .i_din(i_din_b),
        .i_din_valid(i_valid),
        .i_ready(ser_ready),
        .o_ready(des_o_ready_b),
        .ov_dout(des_dout_b),
        .o_dout_valid(des_o_valid_b)
    );

    mult_booth_sync
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) mult_booth_sync_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_a(des_dout_a),
        .iv_b(des_dout_b),
        .ov_prod(prod)
    );

    always @(posedge i_clk) begin
        des_o_valid <= des_o_valid_a & des_o_valid_b;
    end

    serializer_fsm
    #(
        .LENGTH(DATA_WIDTH*2)
    ) serializer_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_din(prod),
        .i_din_valid(des_o_valid),
        .i_ready(i_ready),
        .o_ready(ser_ready),
        .o_dout(o_prod),
        .o_dout_valid(o_valid)
    );

endmodule