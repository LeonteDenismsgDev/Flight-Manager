package msg.flight.manager.services.flights;

import lombok.SneakyThrows;
import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.dtos.flights.attributes.RegisterAttribute;
import msg.flight.manager.persistence.dtos.flights.enums.AttributesClasses;
import msg.flight.manager.persistence.models.flights.DBAttribute;
import msg.flight.manager.persistence.repositories.AttributesRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.AttributesServiceUtils;
import msg.flight.manager.services.utils.SecurityUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;

import static msg.flight.manager.services.utils.AttributesServiceUtils.parseDefaultValue;

@Service
public class AttributeService {
    @Autowired
    private AttributesRepository attributesRepository;
    @Autowired
    private SecurityUserUtil securityUser;

    @Transactional
    public ResponseEntity<String> createAttribute(RegisterAttribute registerAttribute) {
        SecurityUser user = securityUser.getLoggedUser();
        DBAttribute attribute = new DBAttribute();
        try {
            AttributesClasses attributeType = AttributesClasses.valueOf(registerAttribute.getType().toUpperCase());
            attribute.setType(attributeType.name().toLowerCase());
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid  attribute type", HttpStatus.BAD_REQUEST);
        }
        if (registerAttribute.getType().equalsIgnoreCase("OBJECT")) {
            if (registerAttribute.getDefaultValue() == null) {
                return new ResponseEntity<>("You need at least one default value", HttpStatusCode.valueOf(400));
            }
            if (registerAttribute.getDefaultValue().getClass() == LinkedHashMap.class) {
                parseDefaultValue(registerAttribute.getDefaultValue());
                attribute.setDefaultValue(registerAttribute.getDefaultValue());
            } else {
                return new ResponseEntity<>("Default value type incorrect", HttpStatusCode.valueOf(400));
            }
        } else {
            attribute.setDefaultValue(registerAttribute.getDefaultValue());
        }
        attribute.setName(AttributesServiceUtils.createClassAttributeName(registerAttribute.getName()));
        attribute.setGlobalVisibility(registerAttribute.getIsGlobal());
        attribute.setRequired(registerAttribute.getIsRequired());
        attribute.setSearchKeyWords(AttributesServiceUtils.generateSearchKey(registerAttribute.getName(), registerAttribute.getType()));
        attribute.setCreatedBy(user.getUsername());
        attribute.setLabel(registerAttribute.getName());
        attribute.setDescription(registerAttribute.getDescription());
        DBAttribute savedAttribute = attributesRepository.save(attribute);
        if (savedAttribute == null) {
            return new ResponseEntity<>("Unable to save the attribute", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("saved", HttpStatus.OK);
    }

    @Transactional
    public List<AttributeDTO> getAppAttributes() {
        SecurityUser loggedUser = securityUser.getLoggedUser();
        return attributesRepository.applicationAttributes(loggedUser.getUsername());
    }

    public ResponseEntity<String> deleteAttribute(String id) {
        boolean deleted = this.attributesRepository.delete(id);
        if (deleted) {
            return new ResponseEntity<>("Attribute deleted", HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>("Unable to delete attribute", HttpStatusCode.valueOf(400));
        }
    }

    @SneakyThrows
    public ResponseEntity<String> updateAttribute(String id, AttributeDTO attr) {
        attr.setName(AttributesServiceUtils.createClassAttributeName(attr.getLabel()));
        boolean updated = this.attributesRepository.update(id, attr);
        if (updated) {
            return new ResponseEntity<>("Attribute updated", HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>("Unable to update attribute", HttpStatusCode.valueOf(400));
        }
    }
}
