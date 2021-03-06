/*
 * ModeShape (http://www.modeshape.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modeshape.jcr.query.validate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.modeshape.common.annotation.Immutable;
import org.modeshape.jcr.api.query.qom.Operator;
import org.modeshape.jcr.query.model.Ordering;
import org.modeshape.jcr.query.model.QueryCommand;
import org.modeshape.jcr.query.model.SelectorName;
import org.modeshape.jcr.value.PropertyType;

/**
 * The interface used to access the structure being queried and validate a query.
 */
@Immutable
public interface Schemata {

    /**
     * Get the information for the table or view with the supplied name within this schema.
     * <p>
     * The resulting definition is immutable.
     * </p>
     * 
     * @param name the table or view name; may not be null
     * @return the table or view information, or null if there is no such table
     */
    Table getTable( SelectorName name );

    /**
     * Information about a queryable table.
     */
    public interface Table {
        /**
         * Get the name for this table.
         * 
         * @return the table name; never null
         */
        SelectorName getName();

        /**
         * Get the information for a column with the supplied name within this table.
         * <p>
         * The resulting column definition is immutable.
         * </p>
         * 
         * @param name the column name; may not be null
         * @return the column information, or null if there is no such column
         */
        Column getColumn( String name );

        /**
         * Get the queryable columns in this table.
         * 
         * @return the immutable map of immutable column objects by their name; never null
         */
        Map<String, Column> getColumnsByName();

        /**
         * Get the queryable columns in this table.
         * 
         * @return the immutable, ordered list of immutable column objects; never null
         */
        List<Column> getColumns();

        /**
         * Get the queryable columns in this table that should be used in case of "SELECT *".
         * 
         * @return the immutable, ordered list of immutable column objects; never null
         */
        List<Column> getSelectAllColumns();

        /**
         * Get the queryable columns in this table that should be used in case of "SELECT *".
         * 
         * @return the immutable map of immutable column objects by their name; never null
         */
        Map<String, Column> getSelectAllColumnsByName();

        /**
         * Get the collection of keys for this table.
         * 
         * @return the immutable collection of immutable keys; never null, but possibly empty
         */
        Collection<Key> getKeys();

        /**
         * Determine whether this table has a {@link #getKeys() key} that contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this table contains a key using exactly the supplied columns, or false otherwise
         */
        boolean hasKey( Column... columns );

        /**
         * Determine whether this table has a {@link #getKeys() key} that contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this table contains a key using exactly the supplied columns, or false otherwise
         */
        boolean hasKey( Iterable<Column> columns );

        /**
         * Obtain this table's {@link #getKeys() key} that contains exactly those columns listed.
         * <p>
         * The resulting key definition is immutable.
         * </p>
         * 
         * @param columns the columns for the key
         * @return the key that uses exactly the supplied columns, or null if there is no such key
         */
        Key getKey( Column... columns );

        /**
         * Obtain this table's {@link #getKeys() key} that contains exactly those columns listed.
         * <p>
         * The resulting key definition is immutable.
         * </p>
         * 
         * @param columns the columns for the key
         * @return the key that uses exactly the supplied columns, or null if there is no such key
         */
        Key getKey( Iterable<Column> columns );

        /**
         * Determine whether this table allows has extra columns not included in the {@link #getColumns() column list}. This value
         * is used to determine whether columns in a SELECT clause should be validated against the list of known columns.
         * 
         * @return true if there are extra columns, or false if the {@link #getColumns() list of columns} is complete for this
         *         table.
         */
        boolean hasExtraColumns();
    }

    /**
     * Information about a view that is defined in terms of other views/tables.
     */
    public interface View extends Table {
        /**
         * Get the {@link QueryCommand query} that is the definition of the view.
         * 
         * @return the view definition; never null
         */
        QueryCommand getDefinition();
    }

    /**
     * Information about a queryable column.
     */
    public interface Column {
        /**
         * Get the name for this column.
         * 
         * @return the column name; never null
         */
        String getName();

        /**
         * Get the property type for this column.
         * 
         * @return the property type; never null
         */
        String getPropertyTypeName();

        /**
         * Get whether the column can be used in a full-text search.
         * 
         * @return true if the column is full-text searchable, or false otherwise
         */
        boolean isFullTextSearchable();

        /**
         * Get the set of operators that can be used in a comparison involving this column.
         * 
         * @return the operators; never null but possibly empty
         */
        Set<Operator> getOperators();

        /**
         * Get whether this column can be used within an {@link Ordering ORDER BY clause}.
         * 
         * @return true if this column can be used in an order specification, or false otherwise
         */
        boolean isOrderable();

        /**
         * Get whether this column can be used within a comparison using {@link #getOperators() operators} other than
         * {@link Operator#EQUAL_TO} or {@link Operator#NOT_EQUAL_TO}.
         * 
         * @return true if the column can be used in a comparison other than "=" or "!=", or false if only "=" and/or "!=" can be
         *         used
         */
        boolean isComparable();

        PropertyType getRequiredType();

        Object getMinimum();

        Object getMaximum();
    }

    /**
     * Information about a key for a table.
     */
    public interface Key {
        /**
         * Get the columns that make up this key.
         * 
         * @return the key's columns; immutable and never null
         */
        Set<Column> getColumns();

        /**
         * Determine whether this key contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this key contains exactly the supplied columns, or false otherwise
         */
        boolean hasColumns( Column... columns );

        /**
         * Determine whether this key contains exactly those columns listed.
         * 
         * @param columns the columns for the key
         * @return true if this key contains exactly the supplied columns, or false otherwise
         */
        boolean hasColumns( Iterable<Column> columns );
    }

}
