package ru.job4j.cinema.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.repository.FileRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void whenFindByIdExistsThenReturnFile() {
        File expectedFile = new File(1, "test.jpg", "files/test.jpg");
        when(fileRepository.findById(1)).thenReturn(Optional.of(expectedFile));

        Optional<File> actualFile = fileService.findById(1);

        assertThat(actualFile)
                .isPresent()
                .contains(expectedFile);
    }

    @Test
    void whenFindByIdNotExistsThenReturnEmpty() {
        when(fileRepository.findById(999)).thenReturn(Optional.empty());

        Optional<File> actualFile = fileService.findById(999);

        assertThat(actualFile).isEmpty();
    }

    @Test
    void whenFindByIdThenVerifyRepositoryCall() {
        fileService.findById(1);
        verify(fileRepository).findById(1);
    }

    @Test
    void whenFindByIdWithDifferentFilesThenCorrect() {
        File file1 = new File(1, "file1.jpg", "path/to/file1.jpg");
        File file2 = new File(2, "file2.png", "path/to/file2.png");

        when(fileRepository.findById(1)).thenReturn(Optional.of(file1));
        when(fileRepository.findById(2)).thenReturn(Optional.of(file2));

        assertThat(fileService.findById(1).get().getName()).isEqualTo("file1.jpg");
        assertThat(fileService.findById(2).get().getName()).isEqualTo("file2.png");
    }
}