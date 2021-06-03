/**
 * This file is part of GETARI.
 *
 * GETARI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GETARI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GETARI. If not, see <https://www.gnu.org/licenses/>.
 */
package fr.inrae.agroclim.getari.exception;

/**
 * Exception.
 *
 * Last change $Date$
 *
 * @author $Author$
 * @version $Revision$
 */
public class GetariException extends Exception {

    /**
     * UUID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Message for unexpected error.
     */
    private static final String UNEXPECTED_ERROR;

    static {
        UNEXPECTED_ERROR = "Unexpected error occurs the process.";
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later
     * retrieval by the Throwable.getMessage() method.
     */
    public GetariException(final String message) {
        super(message);
    }


    /**
     * Constructs a new exception with the specified cause and a detail message.
     *
     * @param cause the cause (which is saved for later retrieval by the
     * Throwable.getCause() method). (A null value is permitted, and indicates
     * that the cause is nonexistent or unknown.)
     */
    public GetariException(final Throwable cause) {
        super(cause);
    }
    @Override
    public final String getMessage() {
        if (super.getMessage() == null) {
            return UNEXPECTED_ERROR;
        }
        return super.getMessage();
    }

}
