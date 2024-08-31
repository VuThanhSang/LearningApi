package com.example.learning_api.service.core.Impl;

import com.example.learning_api.constant.ErrorConstant;
import com.example.learning_api.dto.common.document.ContentBlock;
import com.example.learning_api.dto.request.document.CreateDocumentRequest;
import com.example.learning_api.dto.request.document.UpdateDocumentRequest;
import com.example.learning_api.entity.sql.database.BlockEntity;
import com.example.learning_api.entity.sql.database.DocumentEntity;
import com.example.learning_api.entity.sql.database.WorkspaceMemberEntity;
import com.example.learning_api.enums.BlockType;
import com.example.learning_api.enums.RoleEnum;
import com.example.learning_api.model.CustomException;
import com.example.learning_api.repository.database.*;
import com.example.learning_api.service.common.ModelMapperService;
import com.example.learning_api.service.core.IDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.*;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService implements IDocumentService {
    private final DocumentRepository documentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ModelMapperService modelMapperService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final BlockRepository blockRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public void createDocument(CreateDocumentRequest body) {
        try {
            if (body.getWorkspaceId() == null)
                throw new Exception("Workspace id is required");
            if (workspaceRepository.findById(body.getWorkspaceId()).isEmpty())
                throw new Exception("Workspace not found");
            if (body.getOwnerRole() == null)
                throw new Exception("Owner role is required");
            if (body.getOwnerRole().equals(RoleEnum.USER))
                studentRepository.findById(body.getOwnerId()).orElseThrow(() -> new Exception("Student not found"));
            else
                teacherRepository.findById(body.getOwnerId()).orElseThrow(() -> new Exception("Teacher not found"));

            WorkspaceMemberEntity workspaceMemberEntity = workspaceMemberRepository
                    .findByWorkspaceIdAndMemberId(body.getWorkspaceId(), body.getOwnerId());
            if (workspaceMemberEntity == null)
                throw new Exception("You are not a member of this workspace");

            List<ContentBlock> content = extractFileContent(body);
            String htmlContent = convertDocxToHtml(body.getFile());
            DocumentEntity documentEntity = modelMapperService.mapClass(body, DocumentEntity.class);
            documentEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            documentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            documentRepository.save(documentEntity);

            // Split content into blocks and save each block
//            List<BlockEntity> blocks = splitContentIntoBlocks(content, documentEntity.getId());
//            blockRepository.saveAll(blocks);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating document: " + e.getMessage());
        }
    }

    public String convertDocxToHtml(MultipartFile file) throws IOException {
        StringBuilder html = new StringBuilder("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; }");
        html.append("</style></head><body>");

        try (InputStream is = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);

            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    html.append(convertParagraphToHtml(paragraph));
                } else if (element instanceof XWPFTable) {
                    XWPFTable table = (XWPFTable) element;
                    html.append(convertTableToHtml(table));
                }
            }
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String convertParagraphToHtml(XWPFParagraph paragraph) {
        StringBuilder paraHtml = new StringBuilder("<p style='");
        // Apply paragraph styles
        if (paragraph.getAlignment() != null) {
            paraHtml.append("text-align:").append(paragraph.getAlignment().toString().toLowerCase()).append(";");
        }
        paraHtml.append("'>");

        for (XWPFRun run : paragraph.getRuns()) {
            String runText = run.getText(0);
            if (runText != null) {
                String runHtml = "<span style='";
                // Apply text styles
                if (run.isBold()) runHtml += "font-weight:bold;";
                if (run.isItalic()) runHtml += "font-style:italic;";
                if (run.getUnderline() != UnderlinePatterns.NONE) runHtml += "text-decoration:underline;";
                if (run.getColor() != null) runHtml += "color:#" + run.getColor() + ";";
                if (run.getFontSize() != -1) {
                    runHtml += "font-size:" + run.getFontSize() + "pt;";
                } else if (run.getFontSizeAsDouble() != -1) {
                    runHtml += "font-size:" + run.getFontSizeAsDouble() + "pt;";
                }
                runHtml += "'>" + runText.replace("\n", "<br/>") + "</span>";
                paraHtml.append(runHtml);
            }

            // Handle images
            List<XWPFPicture> pictures = run.getEmbeddedPictures();
            for (XWPFPicture picture : pictures) {
                String imageData = Base64.getEncoder().encodeToString(picture.getPictureData().getData());
                paraHtml.append("<img src='data:image/png;base64,").append(imageData).append("' />");
            }
        }
        paraHtml.append("</p>");
        return paraHtml.toString();
    }

    private String convertTableToHtml(XWPFTable table) {
        StringBuilder tableHtml = new StringBuilder("<table style='border-collapse:collapse;'>");
        for (XWPFTableRow row : table.getRows()) {
            tableHtml.append("<tr>");
            for (XWPFTableCell cell : row.getTableCells()) {
                tableHtml.append("<td style='border:1px solid black;padding:5px;'>");
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    tableHtml.append(convertParagraphToHtml(paragraph));
                }
                tableHtml.append("</td>");
            }
            tableHtml.append("</tr>");
        }
        tableHtml.append("</table>");
        return tableHtml.toString();
    }
//
//    private List<BlockEntity> splitContentIntoBlocks(String content, String documentId) {
//        List<String> contentBlocks = new ArrayList<>();
//        StringBuilder currentBlock = new StringBuilder();
//        String[] lines = content.split("\n");
//
//        for (String line : lines) {
//            if (line.trim().isEmpty() && currentBlock.length() > 0) {
//                contentBlocks.add(currentBlock.toString().trim());
//                currentBlock = new StringBuilder();
//            } else {
//                currentBlock.append(line).append("\n");
//            }
//        }
//
//        if (currentBlock.length() > 0) {
//            contentBlocks.add(currentBlock.toString().trim());
//        }
//
//        List<BlockEntity> blocks = new ArrayList<>();
//        for (int i = 0; i < contentBlocks.size(); i++) {
//            BlockEntity blockEntity = new BlockEntity();
//            blockEntity.setDocumentId(documentId);
//            blockEntity.setContent(contentBlocks.get(i));
//            blockEntity.setOrder(i);
//            blockEntity.setType(determineBlockType(contentBlocks.get(i)));
//            blockEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
//            blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
//            blocks.add(blockEntity);
//        }
//        return blocks;
//    }
//
//    private BlockType determineBlockType(String content) {
//        String trimmedContent = content.trim();
//        if (trimmedContent.startsWith("#")) {
//            return BlockType.HEADING;
//        } else if (trimmedContent.startsWith("-") || trimmedContent.startsWith("*")) {
//            return BlockType.LIST_ITEM;
//        } else if (trimmedContent.startsWith("```")
//                || trimmedContent.lines().allMatch(line -> line.startsWith("    "))) {
//            return BlockType.CODE;
//        } else if (trimmedContent.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif).*")) {
//            return BlockType.IMAGE;
//        } else if (trimmedContent.contains("|") && trimmedContent.startsWith("|") && trimmedContent.endsWith("|")) {
//            return BlockType.TABLE;
//        } else {
//            return BlockType.TEXT;
//        }
//    }

    private List<ContentBlock> extractFileContent(CreateDocumentRequest body) throws IOException {
        return extractContentFromFile(body.getFile());
    }

    public List<ContentBlock> extractContentFromFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);

        switch (fileExtension) {
            case "pdf":
                return extractContentFromPdf(file);
            case "docx":
                return extractContentFromDocx(file);
            default:
                throw new CustomException(ErrorConstant.FILE_INVALID);
        }
    }

    private List<ContentBlock> extractContentFromPdf(MultipartFile file) throws IOException {
        List<ContentBlock> blocks = new ArrayList<>();
//        try (PDDocument document = PDDocument.load(file.getInputStream())) {
//            PDFTextStripper stripper = new CustomPDFTextStripper();
//            stripper.setSortByPosition(true);
//            stripper.setStartPage(1);
//            stripper.setEndPage(document.getNumberOfPages());
//            stripper.getText(document);
//            blocks = ((CustomPDFTextStripper) stripper).getContentBlocks();
//        }
        return blocks;
    }
    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1).toLowerCase())
                .orElseThrow(() -> new CustomException(ErrorConstant.FILE_INVALID));
    }

    private List<ContentBlock> extractContentFromDocx(MultipartFile file) throws IOException {
        List<ContentBlock> blocks = new ArrayList<>();
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    blocks.addAll(processDocxParagraph(paragraph));
                } else if (element instanceof XWPFTable) {
                    blocks.add(new ContentBlock(BlockType.TABLE, processDocxTable((XWPFTable) element)));
                }
            }
        }
        return blocks;
    }

    private List<ContentBlock> processDocxParagraph(XWPFParagraph paragraph) {
        List<ContentBlock> blocks = new ArrayList<>();
        StringBuilder content = new StringBuilder();
        BlockType type = determineBlockType(paragraph);

        for (XWPFRun run : paragraph.getRuns()) {
            if (run.getEmbeddedPictures().size() > 0) {
                // If there's content before the image, add it as a separate block
                if (content.length() > 0) {
                    blocks.add(new ContentBlock(type, content.toString().trim()));
                    content = new StringBuilder();
                }
                // Process the image
                blocks.add(processDocxImage(run));
            } else {
                String text = run.getText(0);
                if (text != null) {
                    if (run.isBold()) {
                        content.append("**").append(text).append("**");
                    } else if (run.isItalic()) {
                        content.append("*").append(text).append("*");
                    } else {
                        content.append(text);
                    }
                }
            }
        }

        // Add any remaining text content
        if (content.length() > 0) {
            blocks.add(new ContentBlock(type, content.toString().trim()));
        }

        return blocks;
    }

    private ContentBlock processDocxImage(XWPFRun run) {
        XWPFPicture picture = run.getEmbeddedPictures().get(0);
        CTDrawing drawing = run.getCTR().getDrawingArray(0);
        String description = picture.getDescription() != null ? picture.getDescription() : "Unnamed image";

        // Extract image dimensions
        long width = 0;
        long height = 0;
        if (drawing != null && drawing.getInlineArray().length > 0) {
            width = drawing.getInlineArray(0).getExtent().getCx();
            height = drawing.getInlineArray(0).getExtent().getCy();
        }

        // Convert EMUs to inches
        double widthInches = convertEMUToInches(width);
        double heightInches = convertEMUToInches(height);

        String imageInfo = String.format("Image: %s (Width: %.2f inches, Height: %.2f inches)",
                description, widthInches, heightInches);
        return new ContentBlock(BlockType.IMAGE, imageInfo);
    }

    private double convertEMUToInches(long emu) {
        return emu / 914400.0;
    }


    private String processDocxTable(XWPFTable table) {
        StringBuilder tableContent = new StringBuilder();
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                tableContent.append(cell.getText()).append(" | ");
            }
            tableContent.append("\n");
        }
        return tableContent.toString();
    }

    private BlockType determineBlockType(XWPFParagraph paragraph) {
        if (paragraph.getStyle() != null && paragraph.getStyle().startsWith("Heading")) {
            return BlockType.HEADING;
        } else if (paragraph.getNumFmt() != null) {
            return BlockType.LIST_ITEM;
        } else {
            return BlockType.TEXT;
        }
    }
    @Override
    public void updateDocument(UpdateDocumentRequest body) {
        try {
            if (body.getId() == null)
                throw new Exception("Document id is required");
            DocumentEntity documentEntity = documentRepository.findById(body.getId())
                    .orElseThrow(() -> new Exception("Document not found"));
            if (body.getName() != null)
                documentEntity.setName(body.getName());
            if (body.getDescription() != null)
                documentEntity.setDescription(body.getDescription());
            if (body.getStatus() != null)
                documentEntity.setStatus(body.getStatus());
            documentEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            documentRepository.save(documentEntity);

        } catch (Exception e) {
            throw new RuntimeException("Error while updating document: " + e.getMessage());
        }

    }

    @Override
    public void deleteDocument(String documentId) {
        try {
            if (documentId == null)
                throw new Exception("Document id is required");
            if (documentRepository.findById(documentId).isEmpty())
                throw new Exception("Document not found");
            documentRepository.deleteById(documentId);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting document: " + e.getMessage());
        }
    }

    @Override
    public void createBlock(BlockEntity body) {
        try {
            if (body.getDocumentId() == null)
                throw new Exception("Document id is required");
            if (documentRepository.findById(body.getDocumentId()).isEmpty())
                throw new Exception("Document not found");
            BlockEntity blockEntity = modelMapperService.mapClass(body, BlockEntity.class);
            blockEntity.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            blockRepository.save(blockEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating block: " + e.getMessage());
        }
    }

    @Override
    public void updateBlock(BlockEntity body) {
        try {
            if (body.getId() == null)
                throw new Exception("Block id is required");
            BlockEntity blockEntity = blockRepository.findById(body.getId())
                    .orElseThrow(() -> new Exception("Block not found"));
            if (body.getContent() != null)
                blockEntity.setContent(body.getContent());
            if (body.getType() != null)
                blockEntity.setType(body.getType());
            blockEntity.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
            blockRepository.save(blockEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error while updating block: " + e.getMessage());
        }

    }

    @Override
    public void deleteBlock(String blockId) {
        try {
            if (blockId == null)
                throw new Exception("Block id is required");
            if (blockRepository.findById(blockId).isEmpty())
                throw new Exception("Block not found");
            blockRepository.deleteById(blockId);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting block: " + e.getMessage());
        }

    }

    @Override
    public List<DocumentEntity> getDocuments(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentEntity> documents = documentRepository.findByTitleContainingIgnoreCase(search, pageable);
        return documents.getContent();
    }
}
