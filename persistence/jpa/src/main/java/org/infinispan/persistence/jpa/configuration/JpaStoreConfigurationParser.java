package org.infinispan.persistence.jpa.configuration;

import static org.infinispan.persistence.jpa.configuration.JpaStoreConfigurationParser.NAMESPACE;

import org.infinispan.commons.configuration.io.ConfigurationReader;
import org.infinispan.commons.util.Util;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.configuration.parsing.ConfigurationParser;
import org.infinispan.configuration.parsing.Namespace;
import org.infinispan.configuration.parsing.ParseUtils;
import org.infinispan.configuration.parsing.Parser;
import org.kohsuke.MetaInfServices;

/**
 * @author Galder Zamarreño
 * @since 9.0
 */
@MetaInfServices

@Namespace(root = "jpa-store")
@Namespace(uri = NAMESPACE + "*", root = "jpa-store")
public class JpaStoreConfigurationParser implements ConfigurationParser {

   static final String NAMESPACE = Parser.NAMESPACE + "store:jpa:";

   @Override
   public void readElement(ConfigurationReader reader,
                           ConfigurationBuilderHolder holder) {
      Element element = Element.forName(reader.getLocalName());
      switch (element) {
         case JPA_STORE: {
            ConfigurationBuilder builder = holder.getCurrentConfigurationBuilder();
            parseJpaCacheStore(reader, builder.persistence().addStore(JpaStoreConfigurationBuilder.class), holder.getClassLoader());
            break;
         }
         default: {
            throw ParseUtils.unexpectedElement(reader);
         }
      }
   }

   private void parseJpaCacheStore(ConfigurationReader reader, JpaStoreConfigurationBuilder builder, ClassLoader classLoader) {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         ParseUtils.requireNoNamespaceAttribute(reader, i);
         String value = reader.getAttributeValue(i);
         Attribute attribute = Attribute.forName(reader.getAttributeName(i));

         switch (attribute) {
            case ENTITY_CLASS_NAME: {
               Class<?> clazz = Util.loadClass(value, classLoader);
               builder.entityClass(clazz);
               break;
            }
            case BATCH_SIZE: {
               builder.batchSize(Long.valueOf(value));
               break;
            }
            case PERSISTENCE_UNIT_NAME: {
               builder.persistenceUnitName(value);
               break;
            }
            case STORE_METADATA: {
               builder.storeMetadata(Boolean.valueOf(value));
               break;
            }
            default: {
               Parser.parseStoreAttribute(reader, i, builder);
            }
         }
      }

      while (reader.inTag()) {
         Parser.parseStoreElement(reader, builder);
      }
   }

   @Override
   public Namespace[] getNamespaces() {
      return ParseUtils.getNamespaceAnnotations(getClass());
   }
}
