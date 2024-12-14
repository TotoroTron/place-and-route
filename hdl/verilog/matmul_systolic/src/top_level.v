
module top_level 
#(
    parameter DATA_WIDTH = 8,
    parameter M = 4,
    parameter N = 4,
    parameter K = 4
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire iv_din_a[DATA_WIDTH-1:0],
    input wire iv_din_b[DATA_WIDTH-1:0]
);
    localparam ADDR_WIDTH_A = $clog2(M);
    localparam ADDR_WIDTH_B = $clog2(N);

    wire [M-1:0] shift_reg_a; // ram enable signals
    wire [M-1:0] wea;
    wire [ADDR_WIDTH_A] addra;
    wire [DATA_WIDTH-1:0] dina [M-1:0]; // from rams
    wire [DATA_WIDTH-1:0] douta [M-1:0]; // array of final sums

    wire [N-1:0] shift_reg_b; // ram enable signals
    wire [N-1:0] web;
    wire [ADDR_WIDTH_B] addrb;
    wire [DATA_WIDTH-1:0] dinb [N-1:0]; // from rams
    wire [DATA_WIDTH-1:0] douta [N-1:0]; // array of final sums


    always @(posedge i_clk) begin
        if (i_en) begin
            shift_reg_ena[M-1:1] <= shift_reg_ena[M-2:0];
            shift_reg_enb[N-1:1] <= shift_reg_enb[N-2:0];
        end
    end

    generate
        for (i = 0; i < M; i = i + 1) begin : ram_a
            single_port_ram
            #(
                .DATA_WIDTH(DATA_WIDTH),
                .ADDR_WIDTH(ADDR_WIDTH_A)
            ) spram_a_inst (
                .i_clk(i_clk),
                .i_rst(i_rst),
                .i_en(shift_reg_ena[i]),
                .i_we(wea[i]),
                .iv_din(dina[i]),
                .iv_addr(addra),
                .ov_dout(douta)
            );
        end
    endgenerate

    generate
        for (i = 0; i < N; i = i + 1) begin : ram_b
            single_port_ram
            #(
                .DATA_WIDTH(DATA_WIDTH),
                .ADDR_WIDTH(ADDR_WIDTH_B)
            ) spram_b_inst (
                .i_clk(i_clk),
                .i_rst(i_rst),
                .i_en(shift_reg_enb[i]),
                .i_we(web[i]),
                .iv_din(dinb[i]),
                .iv_addr(addrb),
                .ov_dout(doutb)
            );
        end
    endgenerate

    matmul_systolic
    #(
        .DATA_WIDTH(DATA_WIDTH)
    ) matmul_systolic_inst (
        .i_clk(i_clk),
        .i_rst(i_rst),
        .i_en(i_en),
        .iv_a(dina),
        .iv_b(dinb)
    );

endmodule
