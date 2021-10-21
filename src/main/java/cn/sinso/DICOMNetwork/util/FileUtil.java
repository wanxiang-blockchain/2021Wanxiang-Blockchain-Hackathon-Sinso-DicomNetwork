package cn.sinso.DICOMNetwork.util;

import java.io.*;

/**
 * @version V4.0
 * @Title: FileUtil
 * @Company: 成都影达科技有限公司
 * @Description: 描述
 * @author: 周聪
 * @date 2019/1/22 14:48
 */
public class FileUtil {
    //创建文件夹
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {// 判断目录是否存在
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {// 创建目标目录
            System.out.println("创建目录成功！" + destDirName);
            return true;
        } else {
            System.out.println("创建目录失败！");
            return false;
        }
    }

    //移动文件
    public static boolean MovePath(String OrignFile, String NewFile) {
        try {
            File afile = new File(OrignFile);
            File nfile = new File(NewFile);
            if (nfile.exists()) {
                return true;
            }
            if (afile.renameTo(new File(NewFile))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
            {return deleteFile(fileName);}
            else
            { return deleteDirectory(fileName);}
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                // System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                // System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            // System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }
    public static String readFile(String filePath) {
        System.out.println(filePath);
        String strOut = "";
        try {
            File file = new File(filePath);
//            ClassPathResource resource = new ClassPathResource(filePath);
//            Resource resource = new ClassPathResource(filePath);
//            File file = resource.getFile();
            System.out.println(file.getName());
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str = "";
            while ((str = bf.readLine()) != null) {
                strOut += str;
            }
            bf.close();
            inputReader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // 返回数组
        return strOut;
    }

    public static boolean writeFile(String filePath, String str) {
        boolean result = false;
        try {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(str);
            bw.close();
            osw.close();
            fos.close();
            result = true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // 返回数组
        return result;
    }

}
