package server;

import java.io.IOException;
import real.npc.Npc;
import real.npc.NpcFactory;
import server.io.Message;
import server.io.Session;
import real.player.Player;
import real.task.Task;
import real.task.TaskData;

public class MenuController {

    private static MenuController instance;

    public static MenuController getInstance() {
        if (instance == null) {
            instance = new MenuController();
        }
        return instance;
    }

    public void saiHanhTinh(Session session, int idnpc) {
        Message msg = null;
        try {
            msg = new Message(38);
            msg.writer().writeShort(idnpc);
            msg.writer().writeUTF("Con hãy về hành tinh của con mà thể hiện!");
            msg.writer().writeByte(1);
            msg.writer().writeUTF("Đóng");
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {

        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void openMenuNPC(int idnpc, Player player) {
        Npc n = Npc.getByIdAndMap(idnpc, player.zone.map.id);
        if (n != null)
        {
            Task task = TaskData.getTask(player.taskId);
            if(task != null && Service.gI().getNpcQuest(player, task.npc_quest[player.taskIndex]) == idnpc)
            {
                Service.gI().sendPopUpMultiLine(player, idnpc, 0, TaskData.NextTask(player));
                return;
            }
            n.openMenu(player);
            player.setIndexMenu(0);
            player.setNPCMenu(n.tempId);
        }
    }

    public void doSelectMenu(Player player, int npcId, int select) throws IOException {
        Npc npc = null;
        if (npcId == NpcFactory.CON_MEO)
        {
            npc = Npc.getNpc(NpcFactory.CON_MEO);
        }
        else if (npcId == NpcFactory.RONG_THIENG)
        {
            npc = Npc.getNpc(NpcFactory.RONG_THIENG);
        }
        else
        {
            npc = Npc.getByIdAndMap(npcId, player.zone.map.id);
        }
        if (npc != null)
        {
            npc.confirmMenu(player, select);
        }
    }
}
