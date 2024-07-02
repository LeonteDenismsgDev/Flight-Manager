package msg.flight.manager.persistence.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.user.update.UpdateUserDto;
import msg.flight.manager.persistence.dtos.user.update.UserTableResult;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TableResult {
    private List<Document> countResult;
    private List<Document> paginationResult;

    public UserTableResult toUserTableResult(Class<? extends UpdateUserDto> listClass) {
        return UserTableResult.builder()
                .usersCount(countResult.get(0).get("totalCount", Integer.class))
                .page(paginationResult.stream().map(doc -> {
                    try {
                        return convertDocumentToDto(doc, listClass);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()))
                .build();
    }

    private <T> T convertDocumentToDto(Document document, Class<T> clazz) throws Exception {
        T dto = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(dto, document.get(field.getName(), field.getType()));
        }
        return dto;
    }
}
