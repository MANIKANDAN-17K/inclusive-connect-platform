import { Component, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'ic-file-upload',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './file-upload.component.html',
    styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {
    @Input() acceptTypes: string[] = ['image/png', 'image/jpeg', 'image/jpg'];
    @Input() maxSizeMB = 5;
    @Input() buttonText = 'Upload File';
    @Input() dropPlaceholder = 'Drag and drop your file here, or click to browse';
    @Input() isUploading = false;
    @Input() label = 'Upload File';

    @Output() fileSelected = new EventEmitter<File>();

    isDragOver = false;
    errorMessage: string | null = null;

    @HostListener('dragover', ['$event'])
    onDragOver(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        if (!this.isUploading) {
            this.isDragOver = true;
        }
    }

    @HostListener('dragleave', ['$event'])
    onDragLeave(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.isDragOver = false;
    }

    @HostListener('drop', ['$event'])
    onDrop(event: DragEvent): void {
        event.preventDefault();
        event.stopPropagation();
        this.isDragOver = false;

        if (this.isUploading) return;

        const files = event.dataTransfer?.files;
        if (files && files.length > 0) {
            this.handleFile(files[0]);
        }
    }

    onFileChange(event: Event): void {
        const input = event.target as HTMLInputElement;
        if (input.files && input.files.length > 0) {
            this.handleFile(input.files[0]);
            // Reset input value so same file can be selected again
            input.value = '';
        }
    }

    private handleFile(file: File): void {
        this.errorMessage = null;

        // Validate size
        const maxSizeBytes = this.maxSizeMB * 1024 * 1024;
        if (file.size > maxSizeBytes) {
            this.errorMessage = `File is too large. Maximum size is ${this.maxSizeMB} MB.`;
            return;
        }

        // Validate type
        const fileType = file.type.toLowerCase();
        const fileName = file.name.toLowerCase();

        const isAllowedType = this.acceptTypes.some(type => {
            // e.g. "image/png"
            if (type.includes('/')) {
                return fileType === type;
            }
            // e.g. ".pdf" or "pdf"
            const extension = type.startsWith('.') ? type : `.${type}`;
            return fileName.endsWith(extension);
        });

        if (!isAllowedType) {
            const formattedTypes = this.acceptTypes
                .map(t => t.replace('image/', '').replace('application/', '').toUpperCase())
                .join(', ');
            this.errorMessage = `Invalid file format. Only ${formattedTypes} are allowed.`;
            return;
        }

        this.fileSelected.emit(file);
    }
}
