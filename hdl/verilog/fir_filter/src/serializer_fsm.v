

module serializer_fsm
#(
    parameter LENGTH = 24
)(
    input wire i_clk,
    input wire i_rst,
    input wire i_en,
    input wire [LENGTH-1:0] iv_din,
    input wire i_din_valid,
    input wire i_ready,
    output reg o_ready,
    output wire o_dout,
    output reg o_dout_valid
);

    reg [LENGTH-1:0] shift_reg;
    assign o_dout = shift_reg[0];

    localparam LENGTH_BITS = $clog2(LENGTH)+1;
    reg [LENGTH_BITS-1:0] counter = { (LENGTH_BITS){1'b0} };

    parameter 
        IDLE = 4'b0000,
        LOAD = 4'b0001,
        SHIFT_OUT = 4'b0010;
    reg [3:0] state = IDLE;
    reg [3:0] next_state;


    // STATE REGISTER
    always @(posedge i_clk) begin
        if (i_rst) state <= IDLE;
        else if (i_en) state <= next_state;
    end

    // STATE MACHINE
    always @(*) begin
        next_state = state;
        case (state)
            IDLE: begin
                if (i_din_valid) begin
                    next_state = LOAD;
                end
            end

            LOAD: begin
                next_state = SHIFT_OUT;
            end

            SHIFT_OUT: begin
                if (counter == LENGTH - 1) begin
                    next_state = IDLE;
                end
            end

            default: begin
                next_state = IDLE;
            end
        endcase
    end

    // OUTPUT LOGIC
    always @(posedge i_clk) begin
        if (i_rst) begin
            o_ready      <= 1'b0;
            o_dout_valid <= 1'b0;
            counter      <= 0;
            shift_reg    <= 0;
        end else if (i_en) begin
            o_ready      <= 1'b0;
            o_dout_valid <= 1'b0;

            case (state)
                IDLE: begin
                    counter      <= 0;
                    shift_reg    <= 0;
                end

                LOAD: begin
                    o_ready      <= 1'b1;
                    shift_reg    <= iv_din;
                    counter      <= 0;
                end

                SHIFT_OUT: begin
                    o_dout_valid <= 1'b1;
                    // if (o_dout_valid && i_ready) begin
                    if (i_ready) begin
                        counter   <= counter + 1;
                        shift_reg <= {1'b0, shift_reg[LENGTH-1:1]};
                    end
                end

                default: begin
                end
            endcase
        end
    end
endmodule
