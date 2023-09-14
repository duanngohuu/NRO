package real.task;

import java.sql.ResultSet;
import java.util.ArrayList;
import real.item.Item;
import real.item.ItemData;
import real.item.ItemOption;
import real.map.Mob;
import real.player.Player;
import server.SQLManager;
import server.Service;
import server.Util;

public class TaskData {

    public static ArrayList<Task> list_task = new ArrayList<>();

    public static void load_task() {
        list_task.clear();
        ResultSet rs = null;
        try {
            rs = SQLManager.executeQueryDATA("SELECT * FROM task_template ORDER BY `id` ASC");
            while (rs.next()) {
                Task task = new Task();
                task.taskId = rs.getShort("id");
                task.name = rs.getString("name");
                task.detail = rs.getString("detail");
                task.subNames = rs.getString("subNames").split("\n");
                String str = rs.getString("maps").replace("\r",  "");
                String[] maps = str.split("\n");
                task.maps = new int[maps.length][3];
                for(int i = 0; i < maps.length; i++){
                    String[] arr = maps[i].split("_");
                    for(int j = 0; j < 3; j++)
                    {
                        task.maps[i][j] = Integer.valueOf(arr[j]);
                    }
                }
                
                task.counts = Util.parseIntArray(rs.getString("counts").split("_"));
                task.contentInfo = rs.getString("contentInfo").split("\n");
                task.killId = Util.parseIntArray(rs.getString("killId").split("_"));
                task.npc_quest = rs.getString("npc_quest").split("_");
                task.type = rs.getInt("type");
                list_task.add(task);
            }
            Util.warning("finish load task! [" + list_task.size() + "]\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static Task getTask(int idtask) {
        for (Task task : list_task) {
            if (task.taskId == idtask) {
                return task;
            }
        }
        return null;
    }
    
    public static String NextTask(Player pl)
    {
        String str = textChatTASK[pl.taskId];
        Task task = getTask(pl.taskId);
        try
        {
            if (task != null)
            {
                if(pl.taskId == 12 && pl.taskIndex == 0 && pl.clan != null && pl.clan.members.size() >= 5 || (pl.taskIndex + 1) >= task.subNames.length)
                {
                    long tnsm = TaskData.getTask(pl.taskId).getTNSM();
                    if(task.type == 1)
                    {
                        Item it = ItemData.gI().get_item(1184);
                        it.itemOptions.clear();
                        it.itemOptions.add(new ItemOption(30, 0));
                        it.quantity = 54;
                        pl.inventory.addItemBag(it);
                        Service.gI().sendThongBao(pl, "Bạn nhận được "+ it.quantity +" "+it.template.name);
                    }
                    if(task.taskId == 2)
                    {
                        Item item = pl.inventory.findItemBagByTemp(73);
                        if(item != null){
                            pl.inventory.subQuantityItemsBag(item, item.quantity);
                        }
                    }
                    if(task.taskId == 3)
                    {
                        Item item = pl.inventory.findItemBagByTemp(78);
                        if(item != null){
                            pl.inventory.subQuantityItemsBag(item, item.quantity);
                        }
                    }
                    if(task.taskId == 14)
                    {
                        Item item = pl.inventory.findItemBagByTemp(85);
                        if(item != null){
                            pl.inventory.subQuantityItemsBag(item, item.quantity);
                        }
                    }
                    if(task.taskId == 31)
                    {
                        Item item = pl.inventory.findItemBagByTemp(993);
                        if(item != null)
                        {
                            pl.inventory.subQuantityItemsBag(item, item.quantity);
                        }
                    }
                    pl.taskId += 1 + (task.type == 3 ? pl.gender : 0);
                    if(task.taskId == 4 || task.taskId == 5 || task.taskId == 6)
                    {
                        pl.taskId = 7;
                    }
                    if(task.taskId == 9 || task.taskId == 10 || task.taskId == 11)
                    {
                        pl.taskId = 12;
                    }
                    pl.taskIndex = 0;
                    pl.taskCount = 0;
                    pl.inventory.sendItemBags();
                    Service.gI().ClearTask(pl);
                    Service.gI().send_task(pl, TaskData.getTask(pl.taskId));
                    Service.gI().sendBag(pl);
                    Service.gI().congTiemNang(pl, (byte) 0, tnsm);
                    Service.gI().congTiemNang(pl, (byte) 1, tnsm);
                    return str;
                }
                else if((pl.taskIndex + 1) < task.subNames.length)
                {
                    Service.gI().send_task_next(pl);
                    Service.gI().sendBag(pl);
                    return "Thật thú vị đó !!!";
                }
            }
        }
        catch (Exception e) {
            Util.logException(TaskData.class, e);
        }
        return task.subNames[pl.taskIndex];
    }
    
    public static long getSucManh(Player player)
    {
        try
        {
            Task task = getTask(player.taskId);
            String str = task.subNames[player.taskIndex].toLowerCase().replaceAll("[\\D]", "");
            return Long.parseLong(str);
        }
        catch(Exception e){
            Util.logException(TaskData.class, e);
        }
        return -1;
    }
    
    public static void autoChat(Player player, Mob mob, boolean Show){
        String textTrue;
        String textFalse;
        switch (player.taskId) {
            case 8:
                if(player.taskIndex == 1)
                {
                    textTrue = "Bạn đã tìm thấy ngọc rồng 7 sao rồi, hãy chạm nhanh 2 lần vào đối tượng để lấy";
                    textFalse = "Con " + mob.template.name + " này không giữ ngọc, hãy tìm con khác";
                    Service.gI().sendThongBao(player, Show ? textTrue : textFalse);
                }
                break;
            case 14:
                if(player.taskIndex == 1)
                {
                    textTrue = "Bạn đã tìm thấy cuốn truyện rồi, hãy chạm nhanh 2 lần vào đối tượng để lấy";
                    textFalse = "Con " + mob.template.name + " này không giữ truyện, hãy tìm con khác";
                    Service.gI().sendThongBao(player, Show ? textTrue : textFalse);
                }
                break;
        }
    }

    public static String[] textChatTASK = {
        "NV DAU TIEN BAO CAO VƠI NPC TAI NHA",
        "Tốt lắm, giờ con hãy đi thu thập đùi gà cho ta",
        "Đùi gà đây rồi, tốt lắm, haha. Ta sẽ nướng tại đống lửa đằng kia, con có thể ăn bất cứ lúc nào nếu muốn",
        "Có em bé trong phi thuyền rơi xuống à, ta cứ tưởng là sao băng",
        "Tốt lắm, con hoàn thành rất xuất xắc",
        "Tốt lắm, con hoàn thành rất xuất xắc",
        "Tốt lắm, con hoàn thành rất xuất xắc",
        "Tốt lắm, con làm ta bất ngờ đấy. Bây giờ hãy đi lấy lại ngọc đem về đây cho ta",
        "Cháu trai của ông, con làm ông tự hào lắm. Con đã biết dùng sức mạnh của mình để giúp kẻ yếu\n\"Bây giờ con đã trưởng thành thực sự rồi...",
        "Ta không nhận đệ tử đâu. Ồ con tặng ta truyện Doremon hả, thôi được nhưng con phải cố gắng luyện tập đó nhé. Hãy gia nhập một bang hội để luyện tập, sau đó quay lại đây gặp ta",
        "Ta không nhận đệ tử đâu. Ồ con tặng ta truyện Doremon hả, thôi được nhưng con phải cố gắng luyện tập đó nhé. Hãy gia nhập một bang hội để luyện tập, sau đó quay lại đây gặp ta",
        "Ta không nhận đệ tử đâu. Ồ con tặng ta truyện Doremon hả, thôi được nhưng con phải cố gắng luyện tập đó nhé. Hãy gia nhập một bang hội để luyện tập, sau đó quay lại đây gặp ta",
        "Con hãy cùng các thành viên trong bang tiêu diệt cho ta 30 con Heo rừng, 30 con Heo da xanh và 30 con Heo Xayda",
        "Bang của con rất có tinh thần đồng đội, con hãy cùng các thành viên luyện tập chăm chỉ để thành tài nhé",
        "Con đã tìm thấy truyện Doremon tập 2 rồi à, mau đưa cho ta nào",
        "Con và bang hội làm rất tốt, ta có quà dành cho con",
        "Con làm rất tốt, ta có quà dành cho con",
        "Con làm rất tốt, ta có quà dành cho con",
        "Con làm rất tốt, ta có quà dành cho con",
        "Con làm rất tốt, Trung Úy Trắng đã bị tiêu diệt. Ta có quà dành cho con",
        "Tốt lắm, giờ con hãy đi tiêu diệt lũ đệ tử của Fide cho ta",
        "Con làm rất tốt, ta có quà dành cho con",
        "Rất tốt, bọn Fide đã biết sức mạnh của chúng ta",
        "Rất tốt, bọn Fide đã biết sức mạnh thật sự của chúng ta",
        "Cảm ơn cậu đã giải vây cho chúng tôi\nHãy đến thành phố phía nam, đảo balê hoặc cao nguyên tìm và chặn đánh 2 Rôbốt Sát Thủ\nCẩn thận vì bọn chúng rất mạnh",
        "Số 1 chính là bác học Kôrê\nÔng ta đã tự biến mình thành Rôbốt để được bất tử\n2 tên Rôbốt này không phải là Rôbốt sát thủ mà chúng tôi nói đến\nCó thể quá khứ đã thay đổi từ khi cậu đến đây\nMau trở về quá khứ xem chuyện gì đã xảy ra",
        "Bác học Kôrê thật sự là thiên tài\nCả máy tính của ông ta cũng có thể\ntự động tạo ra Rôbốt sát thủ\n2 đứa Rôbốt sát thủ mà chúng tôi nói\ncõ 17, 18 tuổi, 1 trai 1 gái ăn mặc như cao bồi\nBề ngoài thấy hiền lành nhưng ra tay cực kì tàn độc\nCậu phải cẩn thận đừng khinh địch.",
        "Tôi và Ca Lích vừa phát hiện ra 1 vỏ trứng kì lạ đã nở\nGần đó còn có vỏ của một con ve sầu rất to vừa lột xác\nCậu hãy đến thị trấn Ginder tọa độ 213-xyz xem thử\nTôi nghi ngờ nó là 1 tác phẩm nữa của lão Kôrê\nCậu cầm lấy cái này, đó là rađa rò tìm Capsule kì bí\nChúc cậu tìm được vật gì đó thú vị",
        "Hắn sợ chúng ta quá nên bày trò câu giờ đây mà\nCậu hãy tranh thủ 3 ngày này tập luyện để nâng cao sức mạnh bản thân nhé\nCapsule kì bí không chừng lại có ích\nHãy thu thập 1 ít để phòng thân",
        "Chúc mừng cậu đã chiến thắng Siêu Bọ Hung\nCám ơn cậu rất nhiều\nnếu rảnh rỗi cậu hãy đến đây tìm Capsule kì bí nhá",
        "Mabư cuối cùng cũng đã bị tiêu diệt, hòa bình đã đến với toàn cõi vũ trụ, cậu đúng là cứu tinh của nhân loại.",
        "OK bạn",
        "OK bạn",
        "Chưa có nhiệm vụ mới",
        "Chưa có nhiệm vụ mới",
        "Chưa có nhiệm vụ mới",
    };
}
