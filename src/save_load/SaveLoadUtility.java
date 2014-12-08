package save_load;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import save_load.exceptions.JSONLoadException;
import save_load.exceptions.JSONSaveException;
import save_load.exceptions.LoadImageException;
import save_load.exceptions.SaveFileException;
import save_load.exceptions.SaveImageException;
import save_load.exceptions.SaveLoadException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Utility class that handles the loading/saving of JSON files and images.
 * 
 * @author Rahul Harikrishnan
 *
 */

public class SaveLoadUtility {
    private static Gson myGson = new Gson();

    /**
     * Loads a Java class instance from the JSON representation of the same
     * given path to JSON file
     * 
     * @param <T>
     * 
     * @param className
     *            Java class to be loaded
     * @param filePath
     *            to JSON file
     * @return class instance to be loaded
     * @throws SaveLoadException
     */

    public static <T> T loadResource (Class className, String filePath) throws SaveLoadException {
        T jsonRepresentation = null;
        try {
            jsonRepresentation = (T) myGson.fromJson(new FileReader(new File(filePath)), className);
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            throw new JSONLoadException(e, filePath);

        }
        return jsonRepresentation;
    }

    /**
     * Save a JSON representation of a Java class implementing the JSONable
     * interface at the given file path.
     * 
     * @param jsonableClass
     *            class instance that can be saved
     * @param filePath
     *            preferred location to save the JSON file
     * @return actual saved location of the file (for compatibility across
     *         different file systems)
     * @throws SaveLoadException
     */

    public static String save (JSONable jsonableClass, String filePath) throws SaveLoadException {
        filePath = preProcess(filePath);
        FileWriter writer;
        File file = null;
        try {
            file = obtainFile(filePath);
            writer = new FileWriter(file);
            String json = jsonableClass.toJSON();
            writer.write(json);
            writer.close();
        } catch (IOException | SaveLoadException e) {
            throw new JSONSaveException(e, filePath);
        }

        return file.getPath();
    }

    /**
     * Returns the File object reference at the specified file path. Creates
     * file if currently non-existent and required directories.
     * 
     * @param filePath
     * @return File object at specified filepath
     * @throws SaveLoadException
     */
    public static File obtainFile (String filePath) throws SaveLoadException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new SaveFileException(e, filePath);
            }
        }
        return file;
    }

    /**
     * Save's an image from the source file path to the destination file path
     * 
     * @param source
     *            source location of the image
     * @param destination
     *            desired destination location of the image
     * @return actual destination file path of saved image (for compatibility
     *         across different file systems)
     * @throws SaveLoadException
     */
    public static String saveImage (String source, String destination) throws SaveLoadException {
        destination = preProcess(destination);
        File sourceFile = obtainFile(source);
        File destFile = obtainFile(destination);

        try {
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new SaveImageException(e, sourceFile.getName(), destFile.getName());
        }
        return destination;
    }

    private static String preProcess (String source) {
        // Replace spaces with underscores because UNIX directories don't play
        // well with spaces in file paths
        source = source.replaceAll(" ", "_");
        return source;
    }

    /**
     * Loads a Java FX image object at a specified file path.
     * 
     * @param filePath
     *            location of image
     * @return Image
     * @throws SaveLoadException
     */
    public static Image loadImage (String filePath) throws SaveLoadException {
        File imageFile = obtainFile(filePath);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new LoadImageException(e);
        }
        if (bufferedImage != null) {
            WritableImage image = null;
            // Optional second parameter to save pixel data (setting to
            // null)
            image = SwingFXUtils.toFXImage(bufferedImage, null);
            return image;
        }
        throw new SaveLoadException();
    }
}