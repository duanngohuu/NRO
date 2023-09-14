package real.npc;

import real.player.Player;

public interface IAtionNpc {

    void openMenu(Player player);

    void confirmMenu(Player player, int select);

}
