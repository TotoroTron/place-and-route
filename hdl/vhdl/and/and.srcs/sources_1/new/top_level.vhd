library ieee;
use ieee.std_logic_1164.all;

entity top_level is
    port (
        i_btn_0 : in std_logic;
        i_btn_1 : in std_logic;
        o_led : out std_logic
    );
end top_level;

architecture behavioral of top_level is
begin

    o_led <= i_btn_0 AND i_btn_1;

end behavioral;
