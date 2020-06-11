package Digitact.Backend.Util;

import Digitact.Backend.Model.Image.AppImage;
import Digitact.Backend.Model.Image.Block;
import Digitact.Backend.Model.Image.ImageString;
import Digitact.Backend.Model.Image.ImageType;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static Digitact.Backend.Application.*;

public class ImageTools {
  private static int size = (int) BLOCKSIZE; // todo

  public static AppImage createAppImage(String imageString, ImageType it) {
    AppImage appImage = new AppImage();
    appImage.createId();
    appImage.setType(it);
    List<Block> blocks = new LinkedList<Block>();

    List<String> contents = new ArrayList<String>((imageString.length() + size - 1) / size);

    for (int start = 0; start < imageString.length(); start += size) {
      contents.add(imageString.substring(start, Math.min(imageString.length(), start + size)));
    }
    int series = 1;
    for (String content : contents) {
      Block block = new Block();
      block.createId();
      if (storeBlockInFS(block, content)) {
        block.setSeries(series);
        block.setAppImage(appImage);
        blocks.add(block);
        ++series;
      }
    }
    if (blocks.size() == contents.size()) {
      appImage.setBlocks(blocks);
    } else { // repeat the process if a block cant be saved correctly TODO add a max repetition
      createAppImage(imageString, it);
    }
    return appImage;
  }

  private static boolean storeBlockInFS(Block block, String content) {
    File file = new File(absoluteFileSystemPath + block.getID() + blockFormat);
    try {
      if (file.createNewFile()) {
        boolean successful = writeBlock(file, content);
        System.out.println("Block Created!");
        return successful;
      } else System.out.println("Block already exists!");

    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  private static boolean writeBlock(File block, String content) {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(block));
      writer.write(content);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        // Close the writer regardless of what happens...
        writer.close();
      } catch (Exception e) {
      }
    }
    return false;
  }

  public static ImageString combineImage(AppImage image) {
    String content = "";

    List<Block> blocks =
        image.getBlocks().stream()
            .sorted(Comparator.comparingInt(Block::getSeries))
            .collect(Collectors.toList());
    for (Block block : blocks) {
      content += readBlockContent(block);
    }
    return new ImageString(content, image.getType());
  }

  private static String readBlockContent(Block block) {
    BufferedReader reader = null;
    String content = "";
    try {
      reader =
          new BufferedReader(new FileReader(absoluteFileSystemPath + block.getID() + blockFormat));
      StringBuilder stringBuilder = new StringBuilder();
      String line = null;
      //      String ls = System.getProperty("line.separator");
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line);
        //        stringBuilder.append(ls);
      }
      // delete the last new line separator
      //      stringBuilder.deleteCharAt(stringBuilder.length() - 1);

      content = stringBuilder.toString();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return content;
  }
}
