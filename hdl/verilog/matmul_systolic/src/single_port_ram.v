module single_port_ram
#(
    parameter DATA_WIDTH = 24,
    parameter ADDR_WIDTH = 8
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire i_we,
    input wire [ADDR_WIDTH-1:0] iv_addr,
    input wire [DATA_WIDTH-1:0] iv_din,
    output reg [DATA_WIDTH-1:0] ov_dout
);
    reg [DATA_WIDTH-1:0] RAM [2**ADDR_WIDTH-1:0];

    always @(posedge i_clk) begin
        if (i_rst) begin // reset output register
            ov_dout <= 0;
        end else if(i_en) begin
            if (i_we) begin // write
                RAM[addr] <= iv_din;
                ov_dout <= iv_din;
            end else begin // read
                ov_dout <= RAM[addr];
            end
        end
    end

endmodule
