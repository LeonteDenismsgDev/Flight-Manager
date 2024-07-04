package msg.flight.manager.services.flights;

import msg.flight.manager.persistence.dtos.flights.attributes.AttributeDTO;
import msg.flight.manager.persistence.dtos.flights.attributes.RegisterAttribute;
import msg.flight.manager.persistence.dtos.flights.enums.AttributesClasses;
import msg.flight.manager.persistence.models.flights.DBAttribute;
import msg.flight.manager.persistence.repositories.AttributesRepository;
import msg.flight.manager.security.SecurityUser;
import msg.flight.manager.services.utils.AttributesServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttributeService {
    @Autowired
    private AttributesRepository attributesRepository;

    @Transactional
    public ResponseEntity<String> createAttribute(RegisterAttribute registerAttribute) {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        DBAttribute attribute = new DBAttribute();
        try {
            AttributesClasses attributeType = AttributesClasses.valueOf(registerAttribute.getType());
            attribute.setType(attributeType.name());
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid  attribute type", HttpStatus.BAD_REQUEST);
        }
        attribute.setName(AttributesServiceUtils.createClassAttributeName(registerAttribute.getName()));
        attribute.setGlobalVisibility(registerAttribute.isGlobal());
        attribute.setRequired(registerAttribute.isRequired());
        attribute.setDefaultValue(registerAttribute.getDefaultValue());
        attribute.setSearchKeyWords(AttributesServiceUtils.generateSearchKey(registerAttribute.getName(), registerAttribute.getType()));
        attribute.setCreatedBy(user.getUsername());
        attribute.setLabel(registerAttribute.getName());
        attribute.setDescription(registerAttribute.getDescription());
        DBAttribute savedAttribute = attributesRepository.save(attribute);
        return new ResponseEntity<>(savedAttribute.getLabel(), HttpStatus.OK);
    }

    @Transactional
    public List<AttributeDTO> getAppAttributes() {
        SecurityUser loggedUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return attributesRepository.applicationAttributes(loggedUser.getUsername());
    }
}
