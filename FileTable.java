import java.util.Vector;

public class FileTable {

    private Vector table = new Vector();
    private Directory dir;

    public FileTable(Directory var1) {
        this.dir = var1;
    }

    public synchronized FileTableEntry falloc(String var1, String var2) {
        Inode var4 = null;
        short var3;
        while(true) {
            if(var1.equals("/")) {
                var3 = 0;
            } else {
                var3 = this.dir.namei(var1);
            }
            if(var3 >= 0) {
                var4 = new Inode(var3);
                if(var2.compareTo("r") == 0) {
                    if(var4.flag != 0 && var4.flag != 1) {
                        try {
                            this.wait();
                        } catch (InterruptedException var7) {
                            ;
                        }
                        continue;
                    }
                    var4.flag = 1;
                    break;
                }
                if(var4.flag != 0 && var4.flag != 3) {
                    if(var4.flag == 1 || var4.flag == 2) {
                        var4.flag = (short)(var4.flag + 3);
                        var4.toDisk(var3);
                    }
                    try {
                        this.wait();
                    } catch (InterruptedException var6) {
                        ;
                    }
                    continue;
                }
                var4.flag = 2;
                break;
            }
            if(var2.compareTo("r") == 0) {
                return null;
            }
            var3 = this.dir.ialloc(var1);
            var4 = new Inode();
            var4.flag = 2;
            break;
        }
        var4.count++;
        var4.toDisk(var3);
        FileTableEntry var5 = new FileTableEntry(var4, var3, var2);
        this.table.addElement(var5);
        return var5;
    }

    public synchronized boolean ffree(FileTableEntry var1) {
        if(this.table.removeElement(var1)) {
            var1.inode.count--;
            switch(var1.inode.flag) {
            case 1:
                var1.inode.flag = 0;
                break;
            case 2:
                var1.inode.flag = 0;
            case 3:
            default:
                break;
            case 4:
                var1.inode.flag = 3;
                break;
            case 5:
                var1.inode.flag = 3;
            }
            var1.inode.toDisk(var1.iNumber);
            var1 = null;
            this.notify();
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean fempty() {
        return this.table.isEmpty();
    }
}