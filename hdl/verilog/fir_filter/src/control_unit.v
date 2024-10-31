
module control_unit
#(parameter DATA_WIDTH = 24)
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_en,
        input wire i_rx_end,
        output wire o_des_valid,
        output wire o_fir_valid 
    );

    reg [7:0] counter = 8'h00;


endmodule
