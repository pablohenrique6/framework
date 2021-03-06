/*
 * Demoiselle Framework
 *
 * License: GNU Lesser General Public License (LGPL), version 3 or later.
 * See the lgpl.txt file in the root directory or <https://www.gnu.org/licenses/lgpl.html>.
 */
package org.demoiselle.jee.core.lifecycle.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Identifies a method eligible to be executed automatically during <b>application finalization</b>.
 * <p>
 * Take a look at the following usage sample:
 * </p>
 * 
 * <pre>
 * public class MyClass {
 * 
 *  &#064;Shutdown
 *  &#064;Priority(Priority.MIN_PRIORITY)
 *  public void finalize() {
 *    ...
 *  }
 * }
 * </pre>
 * 
 * See {@link DemoiselleLifecyclePriority}
 * 
 * @author SERPRO 
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Shutdown {

}
