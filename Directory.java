public class Directory {

    private static int maxChars = 30;
    private int[] fsizes;
    private char[][] fnames;

    public Directory(int var1) {
        this.fsizes = new int[var1];
        for(int var2 = 0; var2 < var1; ++var2) {
            this.fsizes[var2] = 0;
        }
        this.fnames = new char[var1][maxChars];
        String var3 = "/";
        this.fsizes[0] = var3.length();
        var3.getChars(0, this.fsizes[0], this.fnames[0], 0);
    }

    public void bytes2directory(byte[] var1) {
        int var2 = 0;
        int var3;
        for(var3 = 0; var3 < this.fsizes.length; var2 += 4) {
            this.fsizes[var3] = SysLib.bytes2int(var1, var2);
            var3++;
        }
        for(var3 = 0; var3 < this.fnames.length; var2 += maxChars * 2) {
            String var4 = new String(var1, var2, maxChars * 2);
            var4.getChars(0, this.fsizes[var3], this.fnames[var3], 0);
            var3++;
        }
    }

    public byte[] directory2bytes() {
        byte[] var1 = new byte[this.fsizes.length * 4 + this.fnames.length * maxChars * 2];
        int var2 = 0;
        int var3;
        for(var3 = 0; var3 < this.fsizes.length; var2 += 4) {
            SysLib.int2bytes(this.fsizes[var3], var1, var2);
            var3++;
        }
        for(var3 = 0; var3 < this.fnames.length; var2 += maxChars * 2) {
            String var4 = new String(this.fnames[var3], 0, this.fsizes[var3]);
            byte[] var5 = var4.getBytes();
            System.arraycopy(var5, 0, var1, var2, var5.length);
            var3++;
        }
        return var1;
    }

    public short ialloc(String var1) {
        for(short var2 = 1; var2 < this.fsizes.length; ++var2) {
            if(this.fsizes[var2] == 0) {
                this.fsizes[var2] = Math.min(var1.length(), maxChars);
                var1.getChars(0, this.fsizes[var2], this.fnames[var2], 0);
                return var2;
            }
        }
        return (short)-1;
    }

    public boolean ifree(short var1) {
        if(this.fsizes[var1] > 0) {
            this.fsizes[var1] = 0;
            return true;
        } else {
            return false;
        }
    }

    public short namei(String var1) {
        for(short var2 = 0; var2 < this.fsizes.length; ++var2) {
            if(this.fsizes[var2] == var1.length()) {
                String var3 = new String(this.fnames[var2], 0, this.fsizes[var2]);
                if(var1.compareTo(var3) == 0) {
                    return var2;
                }
            }
        }
        return (short)-1;
    }
}
