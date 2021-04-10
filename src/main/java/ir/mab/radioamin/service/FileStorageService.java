package ir.mab.radioamin.service;

import ir.mab.radioamin.exception.FileStorageException;
import ir.mab.radioamin.model.StorageType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileStorageService {

    private final String baseUrl;

    public FileStorageService() {
        Date date = new Date();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMMM dd");
        yearFormat.format(date);
        monthDayFormat.format(date);
        baseUrl = "/Users/mab/IdeaProjects/radioamin" + "/uploads/" + yearFormat.format(date) + "/" + monthDayFormat.format(date);
    }

    public void storeFile(StorageType storageType, String attrName, MultipartFile file) {

        final Path fileStorageLocation;
        final String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        switch (storageType) {
            case USER_AVATAR:
                fileStorageLocation = Paths.get(baseUrl + "/avatars/users/" + attrName)
                        .toAbsolutePath().normalize();
                break;

            case SINGER_AVATAR:
                fileStorageLocation = Paths.get(baseUrl + "/avatars/singers/" + attrName)
                        .toAbsolutePath().normalize();
                break;
            case MUSIC_COVER:
                fileStorageLocation = Paths.get(baseUrl + "/covers/musics/" + attrName)
                        .toAbsolutePath().normalize();
                break;
            case ALBUM_COVER:
                fileStorageLocation = Paths.get(baseUrl + "/covers/albums/" + attrName)
                        .toAbsolutePath().normalize();
                break;
            case PLAYLIST_COVER:
                fileStorageLocation = Paths.get(baseUrl + "/covers/playlists/" + attrName)
                        .toAbsolutePath().normalize();
                break;
            case MUSIC:
                fileStorageLocation = Paths.get(baseUrl + "/musics/" + attrName)
                        .toAbsolutePath().normalize();
                break;
            case VIDEO:
                fileStorageLocation = Paths.get(baseUrl + "/videos/" + attrName)
                        .toAbsolutePath().normalize();
                break;

            default:
                fileStorageLocation = Paths.get(baseUrl + "/defaults/")
                        .toAbsolutePath().normalize();
        }

        try {
            Path path = Files.createDirectories(fileStorageLocation);
            System.out.println(path);
        } catch (IOException e) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
        }

        Path targetLocation = fileStorageLocation.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }

    }
}
