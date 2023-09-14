package real.task;

import java.util.ArrayList;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import server.Util;

public class TaskBoMongManager {

    public static ArrayList<TaskBoMong> tasks;

    private static TaskBoMongManager instance;

    public static TaskBoMongManager gI() {
        if (instance == null) {
            instance = new TaskBoMongManager();
        }
        return instance;
    }

    public void init() {
        tasks = null;
        tasks = TaskBoMongDAO.load();
        Util.warning("Finish load task bo mong! [" + tasks.size() + "]\n");
    }

    public int getRuby(int task_id) {
        return tasks.get(task_id).money;
    }

    public Item get_item_thuong(int task_id) {
        Item it = new Item();
        switch (task_id) {
            case 1:
                it.template = ItemData.gI().getTemplate((short) 863);
                it.content = it.getContent();
                it.quantity = 1;
                it.itemOptions.add(new ItemOption(73, (short) 0));
                it.itemOptions.add(new ItemOption(107, (short) 8));
                it.itemOptions.add(new ItemOption(93, (short) 15));
                //it.id = it.template.id;
                break;

            case 2:
                it.template = ItemData.gI().getTemplate((short) 864);
                it.content = it.getContent();
                it.quantity = 1;
                it.itemOptions.add(new ItemOption(73, (short) 0));
                it.itemOptions.add(new ItemOption(107, (short) 8));
                it.itemOptions.add(new ItemOption(93, (short) 15));
                //it.id = it.template.id;
                break;

            default:
                it = null;
                break;
        }

        return it;
    }
}
