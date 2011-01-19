/*
 * JBoss, Home of Professional Open Source Copyright 2010, Red Hat, Inc., and
 * individual contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.jboss.logging.model;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JMod;
import com.sun.codemodel.internal.JVar;
import org.jboss.logging.Message;

import javax.lang.model.element.ExecutableElement;
import java.io.Serializable;

/**
 * An abstract code model to create the source file that implements the
 * interface.
 * 
 * <p>
 * Essentially this uses the com.sun.codemodel.internal.JCodeModel to generate the
 * source files with. This class is for convenience in generating default source
 * files.
 * </p>
 * 
 * @author James R. Perkins Jr. (jrp)
 * 
 */
public abstract class ImplementationClassModel extends ClassModel {

    private static final String ID_FIELD_NAME_SUFFIX = "Id";

    private final ImplementationType type;

    protected MethodDescriptor methodDescriptor;

    /**
     * Class constructor.
     * 
     * @param interfaceName
     *            the interface name to implement.
     * @param projectCode
     *            the project code to prepend messages with.
     * @param type
     *            the type of the implementation.
     */
    protected ImplementationClassModel(final String interfaceName, final String projectCode, final ImplementationType type) {
        super(interfaceName + type, projectCode, Object.class.getName(), interfaceName, Serializable.class.getName());

        this.type = type;
        this.methodDescriptor = new MethodDescriptor();
    }

    /**
     * Returns the implementation type.
     * 
     * @return the implementation type.
     */
    public final ImplementationType getType() {
        return type;
    }

    /**
     * Adds a method to the class.
     * 
     * @param method
     *            the method to add.
     */
    public void addMethod(final ExecutableElement method) {
        methodDescriptor = methodDescriptor.add(method);
    }

    /**
     * Adds and id variable to the generated class if the id is greater than 0.
     *
     * <p>
     * The variable name will be the method name with &quot;Id&quot; as the
     * suffix.
     * </p>
     *
     * @param methodName the method name to prefix the id with.
     * @param id         the id of the message.
     *
     * @return the variable that was created or {@code null} if no variable was
     *         created.
     */
    protected JVar addIdVar(final String methodName, final int id) {
        final String idFieldName = methodName + ID_FIELD_NAME_SUFFIX;
        JVar idVar = getDefinedClass().fields().get(idFieldName);
        if (idVar == null && id > Message.NONE) {
            // Create the message id field
            idVar = getDefinedClass().field(
                    JMod.PROTECTED | JMod.STATIC | JMod.FINAL,
                    String.class, idFieldName);
            idVar.init(JExpr.lit(ClassModelUtil.formatMessageId(
                    getProjectCode(), id)));
        }
        return idVar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JCodeModel generateModel() throws IllegalStateException {
        JCodeModel codeModel = super.generateModel();

        // Add the serializable UID
        JFieldVar serialVersionUID = getDefinedClass().field(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, codeModel.LONG, "serialVersionUID");
        serialVersionUID.init(JExpr.lit(1L));

        return codeModel;
    }
}