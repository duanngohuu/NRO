package real.task;

import real.item.Item;
import real.player.Player;
import server.Service;
import server.io.Message;

public class TaskBoMongService {

    private static TaskBoMongService instance;

    public static TaskBoMongService gI() {
        if (instance == null) {
            instance = new TaskBoMongService();
        }
        return instance;
    }

    public void OpenMenu(Player pl) {
        if (pl.bo_mong_finish == null) {
            return;
        }

        Message msg = null;
        try {
            msg = new Message(-76);
            msg.writer().writeByte(0);
            msg.writer().writeByte(TaskBoMongManager.tasks.size());
            for (int i = 0; i < TaskBoMongManager.tasks.size(); i++) {
                TaskBoMong t = TaskBoMongManager.tasks.get(i);
                msg.writer().writeUTF(t.info1);
                msg.writer().writeUTF(t.info2);
                msg.writer().writeShort(t.money);
                msg.writer().writeBoolean(pl.bo_mong_finish[i] == 1);
                msg.writer().writeBoolean(pl.bo_mong_reviece[i] == 1);
            }
            pl.sendMessage(msg);
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void getArchivemnt(Player pl, int index) {
        if (pl.bo_mong_finish == null) {
            return;
        }
        else if (pl.bo_mong_reviece[index] == 1)
        {
            return;
        }
        else
        {
            pl.bo_mong_reviece[index] = 1;
            TaskBoMongDAO.update_task_process(pl, index);
            OpenMenu(pl);
            int ruby = TaskBoMongManager.gI().getRuby(index);
            if (ruby > 0) {
                pl.inventory.ruby += ruby;
                Service.gI().sendMoney(pl);
                Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được " + ruby + " hồng ngọc");
            }

            Item item = TaskBoMongManager.gI().get_item_thuong(index);
            if (item != null) {
                pl.inventory.addItemBag(item);
                pl.inventory.sortItemBag();
                pl.inventory.sendItemBags();
                Service.gI().sendThongBao(pl, "Chúc mừng bạn đã nhận được " + item.template.name);
            }
        }
    }
}
