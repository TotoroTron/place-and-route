library ieee;
use ieee.std_logic_1164.all;

entity top_level is
    port (
        i_btn_0 : in std_logic;
        i_btn_1 : in std_logic;
        o_led : out std_logic
    );
    attribute io_buffer_type : string;
    attribute io_buffer_type of i_btn_0 : signal is "IBUF";
    attribute io_buffer_type of i_btn_1 : signal is "IBUF";
    attribute io_buffer_type of o_led : signal is "OBUF";

end top_level;

architecture behavioral of top_level is
begin

    o_led <= i_btn_0 AND i_btn_1;

end behavioral;
