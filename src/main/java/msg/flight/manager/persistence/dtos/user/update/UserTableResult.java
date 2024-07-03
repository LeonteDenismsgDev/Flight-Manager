package msg.flight.manager.persistence.dtos.user.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import msg.flight.manager.persistence.dtos.TableResult;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserTableResult {
    private Integer usersCount;
    private List<UpdateUserDto> page;

}
