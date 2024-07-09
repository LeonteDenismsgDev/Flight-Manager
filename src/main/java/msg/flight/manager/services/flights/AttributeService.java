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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        if(registerAttribute.getType().equals("CUSTOM")) {
            if (registerAttribute.getDefaultValue() == null) {
                return new ResponseEntity<>("You need at least one default value", HttpStatusCode.valueOf(400));
            }
            if(registerAttribute.getDefaultValue().getClass() == LinkedHashMap.class) {
                attribute.setDefaultValue(registerAttribute.getDefaultValue());
            }
            else {
                return new ResponseEntity<>("Default value type incorrect", HttpStatusCode.valueOf(400));
            }
        }else{
            attribute.setDefaultValue(registerAttribute.getDefaultValue());
        }
        attribute.setName(AttributesServiceUtils.createClassAttributeName(registerAttribute.getName()));
        attribute.setGlobalVisibility(registerAttribute.isGlobal());
        attribute.setRequired(registerAttribute.isRequired());
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

    public ResponseEntity<String> deleteAttr(String id){
        boolean deleted = this.attributesRepository.delete(id);
        if(deleted) {
            return new ResponseEntity<>("Attribute deleted", HttpStatusCode.valueOf(200));
        }else{
            return new ResponseEntity<>("Unable to delete attribute", HttpStatusCode.valueOf(400));
        }
    }

    public ResponseEntity<String> updateAttr(String id, AttributeDTO attr){
        try {
            boolean updated = this.attributesRepository.update(id, attr);
            if (updated) {
                return new ResponseEntity<>("Attribute updated", HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>("Unable to update attribute", HttpStatusCode.valueOf(400));
            }
        }catch(Exception ex){
            return new ResponseEntity<>(ex.getMessage(), HttpStatusCode.valueOf(400));
        }
    }
}
