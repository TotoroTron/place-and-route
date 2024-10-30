
module control_unit
#(parameter LENGTH = 24)
    (
        input wire i_clk,
        input wire i_rst,
        input wire i_en,
        input wire i_rx_end,
        output wire o_des_valid,
        output wire o_fir_valid 
    );

    reg unsigned [7:0] counter = 8'h00;

    always @(posedge i_clk) begin
        i_rx_start = 0;
        if (i_rst) begin
            counter = 0;
        end else if (i_en) begin
            if (counter == 8'd24) begin
                counter = 0;
                i_rx_start = 1;
            end else begin
                counter = counter + 1;
            end
        end
    end

endmodule
